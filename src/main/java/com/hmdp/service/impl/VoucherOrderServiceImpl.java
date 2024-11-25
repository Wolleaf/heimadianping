package com.hmdp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.constant.MessageConstants;
import com.hmdp.constant.RedisConstants;
import com.hmdp.entity.VoucherOrder;
import com.hmdp.exception.VoucherException;
import com.hmdp.mapper.SeckillVoucherMapper;
import com.hmdp.mapper.VoucherOrderMapper;
import com.hmdp.service.IVoucherOrderService;
import com.hmdp.utils.RedisIdGenerator;
import com.hmdp.utils.UserHolder;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
@RequiredArgsConstructor
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder> implements IVoucherOrderService {

    private static final DefaultRedisScript<Long> SECKILL_SCRIPT;
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    static {
        SECKILL_SCRIPT = new DefaultRedisScript<>();
        SECKILL_SCRIPT.setLocation(new ClassPathResource("seckill.lua"));
        SECKILL_SCRIPT.setResultType(Long.class);
    }

    private final SeckillVoucherMapper seckillVoucherMapper;
    private final RedisIdGenerator redisIdGenerator;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;
    private IVoucherOrderService proxy;

    @PostConstruct
    private void init() {
        executorService.submit(new Runnable() {
            String queueName = "stream.orders";
            @Override
            public void run() {
                while (true) {
                    try {
                        // 获取消息队列中的订单
                        List<MapRecord<String, Object, Object>> read = stringRedisTemplate.opsForStream().read(
                                Consumer.from("g1", "c1"),
                                StreamReadOptions.empty().count(1L).block(Duration.ofSeconds(2L)),
                                StreamOffset.create(queueName, ReadOffset.lastConsumed())
                        );
                        // 判断是否有消息
                        if (read == null || read.isEmpty()) {
                            continue;
                        }
                        // 有消息就处理订单
                        MapRecord<String, Object, Object> entries = read.get(0);
                        Map<Object, Object> value = entries.getValue();
                        VoucherOrder voucherOrder = new VoucherOrder();
                        BeanUtil.fillBeanWithMap(value, voucherOrder, false);
                        handleVoucherOrder(voucherOrder);
                        // 处理完成后发送ack
                        stringRedisTemplate.opsForStream().acknowledge(queueName, "g1", entries.getId());
                    } catch (Exception e) {
                        log.error("处理订单异常", e);
                        handlePendingList();
                    }
                }
            }

            private void handlePendingList() {
                while (true) {
                    try {
                        List<MapRecord<String, Object, Object>> read = stringRedisTemplate.opsForStream().read(
                                Consumer.from("g1", "c1"),
                                StreamReadOptions.empty().count(1L),
                                StreamOffset.create(queueName, ReadOffset.from("0"))
                        );
                        if (read == null || read.isEmpty()) {
                            break;
                        }
                        // 有消息就处理订单
                        MapRecord<String, Object, Object> entries = read.get(0);
                        Map<Object, Object> value = entries.getValue();
                        VoucherOrder voucherOrder = new VoucherOrder();
                        BeanUtil.fillBeanWithMap(value, voucherOrder, false);
                        handleVoucherOrder(voucherOrder);
                        // 处理完成后发送ack
                        stringRedisTemplate.opsForStream().acknowledge(queueName, "g1", entries.getId());
                    } catch (Exception e) {
                        log.error("处理订单异常", e);
                    }
                }
            }
        });
    }

    private void handleVoucherOrder(VoucherOrder voucherOrder) {
        Long userId = voucherOrder.getUserId();
        RLock lock = redissonClient.getLock(RedisConstants.LOCK_ORDER_KEY + userId);
        boolean flag = lock.tryLock();
        if (!flag) {
            throw new VoucherException(MessageConstants.REPEAT_SECKILL);
        }
        try {
            // 获取事务代理对象
            proxy.saveVoucherOrder(voucherOrder);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Long seckillVoucher(Long voucherId) {
        // 执行lua脚本，判断是否抢券成功
        Long userId = UserHolder.getUser().getId();
        Long orderId = redisIdGenerator.nextId("order");
        long result = stringRedisTemplate.execute(SECKILL_SCRIPT, Collections.emptyList(),
                voucherId.toString(), userId.toString(), orderId.toString());
        if (result != 0) {
            if (result == 1) throw new VoucherException(MessageConstants.SECKILL_STOCK_EMPTY);
            else throw new VoucherException(MessageConstants.REPEAT_SECKILL);
        }
        // 添加到阻塞队列当中
        proxy = (IVoucherOrderService) AopContext.currentProxy();
        return orderId;
    }

/*    @Override
    public Long seckillVoucher(Long voucherId) {
        SeckillVoucher seckillVoucher = seckillVoucherMapper.selectById(voucherId);
        LocalDateTime now = LocalDateTime.now();
        // 判断秒杀是否开始或结束
        if (now.isBefore(seckillVoucher.getBeginTime())) {
            throw new VoucherException(MessageConstants.SECKILL_NOT_BEGIN);
        }
        if (now.isAfter(seckillVoucher.getEndTime())) {
            throw new VoucherException(MessageConstants.SECKILL_ALREADY_END);
        }
        // 判断库存是否充足
        if (seckillVoucher.getStock() < 1) {
            throw new VoucherException(MessageConstants.SECKILL_STOCK_EMPTY);
        }
        Long userId = UserHolder.getUser().getId();
        // 创建锁对象

    / /        SimpleRedisLock lock = new SimpleRedisLock(RedisConstants.LOCK_ORDER_KEY + userId, stringRedisTemplate);
        RLock lock = redissonClient.getLock(RedisConstants.LOCK_ORDER_KEY + userId);
        boolean flag = lock.tryLock();
        if (!flag) {
            throw new VoucherException(MessageConstants.REPEAT_SECKILL);
        }
        try {
            // 获取事务代理对象
            IVoucherOrderService proxy = (IVoucherOrderService) AopContext.currentProxy();
            return proxy.saveVoucherOrder(voucherId);
        } finally {
            lock.unlock();
        }
    }*/

    @Transactional
    public void saveVoucherOrder(VoucherOrder voucherOrder) {
        // 判断用户是否已经购买过该优惠券
        Long userId = voucherOrder.getUserId();
        Long voucherId = voucherOrder.getVoucherId();
        Integer count = lambdaQuery().eq(VoucherOrder::getUserId, userId)
                .eq(VoucherOrder::getVoucherId, voucherId)
                .count();
        if (count > 0) {
            throw new VoucherException(MessageConstants.ONE_PER_PERSON);
        }
        // 扣减库存, 判断是否扣减成功
        int flag = seckillVoucherMapper.updateStock(voucherId);
        if (flag < 1) {
            throw new VoucherException(MessageConstants.ORDER_FAILED);
        }
        baseMapper.insert(voucherOrder);
    }
}
