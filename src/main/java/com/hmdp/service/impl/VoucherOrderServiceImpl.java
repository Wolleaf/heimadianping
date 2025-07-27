package com.hmdp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.constant.KafkaTopicConstants;
import com.hmdp.constant.MessageConstants;
import com.hmdp.domain.dto.SeckillVoucherOrderDTO;
import com.hmdp.domain.entity.VoucherOrder;
import com.hmdp.exception.VoucherException;
import com.hmdp.mapper.SeckillVoucherMapper;
import com.hmdp.mapper.VoucherOrderMapper;
import com.hmdp.service.IVoucherOrderService;
import com.hmdp.utils.RedisIdGenerator;
import com.hmdp.utils.UserHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

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

    static {
        SECKILL_SCRIPT = new DefaultRedisScript<>();
        SECKILL_SCRIPT.setLocation(new ClassPathResource("seckill.lua"));
        SECKILL_SCRIPT.setResultType(Long.class);
    }

    private final SeckillVoucherMapper seckillVoucherMapper;
    private final RedisIdGenerator redisIdGenerator;
    private final StringRedisTemplate stringRedisTemplate;
    private final KafkaTemplate<String, SeckillVoucherOrderDTO> kafkaTemplate;

    @Override
    public Long seckillVoucher(Long voucherId) {
        // 执行lua脚本，判断是否抢券成功
        Long userId = UserHolder.getUser().getId();
        long result = stringRedisTemplate.execute(SECKILL_SCRIPT, Collections.emptyList(),
                voucherId.toString(), userId.toString());
        if (result != 0) {
            if (result == 1) throw new VoucherException(MessageConstants.SECKILL_STOCK_EMPTY);
            else throw new VoucherException(MessageConstants.REPEAT_SECKILL);
        }
        // 抢券成功，发送到消息队列中异步处理
        Long orderId = redisIdGenerator.nextId("order");
        SeckillVoucherOrderDTO seckillVoucherOrderDTO = new SeckillVoucherOrderDTO(orderId, voucherId, userId);
        kafkaTemplate.send(KafkaTopicConstants.SECKILL_VOUCHER_ORDER, seckillVoucherOrderDTO);
        return orderId;
    }

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
