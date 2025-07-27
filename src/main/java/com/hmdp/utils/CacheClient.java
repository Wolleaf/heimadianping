package com.hmdp.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Slf4j
@Component
@RequiredArgsConstructor
public class CacheClient {

    private final StringRedisTemplate stringRedisTemplate;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    /**
     * 缓存并设置过期时间
     * @param key
     * @param value
     * @param time
     * @param unit
     */
    public void set(String key, Object value, Long time, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value), time, unit);
    }

    /**
     * 缓存并设置逻辑过期
     * @param key
     * @param value
     * @param time
     * @param unit
     */
    public void setWithLogicExpire(String key, Object value, Long time, TimeUnit unit) {
        RedisData redisData = new RedisData();
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(unit.toSeconds(time)));
        redisData.setData(value);
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(redisData));
    }

    /**
     * 解决缓存穿透问题
     * @param key
     * @param type
     * @param id
     * @param daFallback
     * @param time
     * @param unit
     * @return
     * @param <R>
     * @param <T>
     */
    public <R, T> R getWithCachePenetration(
            String key, Class<R> type, T id, Function<T, R> daFallback, Long time, TimeUnit unit) {
        String json = stringRedisTemplate.opsForValue().get(key);
        // 不为空则封装返回, 两种情况：null和""
        if (StrUtil.isNotBlank(json)) {
            return JSONUtil.toBean(json, type);
        }
        // 如果是""，则说明是空缓存(解决缓存穿透)，直接返回null
        if (json != null) {
            return null;
        }
        // null 则查询数据库，并写入缓存，查询数据库函数自定义怎么查
        R r = daFallback.apply(id);
        if (r == null) {
            // 解决缓存穿透
            this.set(key, "", time, unit);
            return null;
        }
        // 存在则写入缓存，并设置过期时间
        this.set(key, r, time, unit);
        return r;
    }

    /**
     * 逻辑过期解决缓存击穿问题
     * @param key
     * @param type
     * @param id
     * @param daFallback
     * @param time
     * @param unit
     * @return
     * @param <R>
     * @param <T>
     */
    public <R, T> R getWithLogicExpire(
            String key, Class<R> type, T id, Function<T, R> daFallback, Long time, TimeUnit unit) {
        String json = stringRedisTemplate.opsForValue().get(key);
        // 空，返回，热点数据都是会提前缓存的
        if (StrUtil.isBlank(json)) {
            return null;
        }
        // 转为RedisData
        RedisData redisData = JSONUtil.toBean(json, RedisData.class);
        // 获取对象
        R r = JSONUtil.toBean((JSONObject) redisData.getData(), type);
        // 判断是否过期
        if (redisData.getExpireTime().isAfter(LocalDateTime.now())) {
            // 没过期就返回
            return r;
        }
        // 过期了就获取互斥锁，开启新线程更新缓存
        String lockKey = "lock:" + key;
        if (tryLock(lockKey)) {
            executorService.submit(() -> {
                try {
                    // 查询最新数据
                    R temp = daFallback.apply(id);
                    RedisData data = new RedisData();
                    data.setExpireTime(LocalDateTime.now().plusSeconds(unit.toSeconds(time)));
                    data.setData(temp);
                    stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(data));
                } finally {
                    unlock(lockKey);
                }
            });
        }
        return r;
    }

    private boolean tryLock(String key) {
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", 10, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(flag);
    }

    private void unlock(String key) {
        stringRedisTemplate.delete(key);
    }
}
