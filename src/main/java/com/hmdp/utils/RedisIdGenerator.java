package com.hmdp.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class RedisIdGenerator {

    private final StringRedisTemplate stringRedisTemplate;
    private static final long BEGIN_TIMESTAMP = 1704067200L;
    private static final String KEY_PREFIX = "incr:";
    private static final int BIT_SHIFT = 32;

    public Long nextId(String keyPrefix) {
        // 1.生成时间戳
        long currentSecond = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        long timestamp = currentSecond - BEGIN_TIMESTAMP;
        // 2.生成序列号
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
        Long increment = stringRedisTemplate.opsForValue().increment(KEY_PREFIX + keyPrefix + date);
        if (increment == null) {
            increment = 0L;
        }
        // 3.拼接并返回
        return (timestamp << BIT_SHIFT) | increment;
    }
}
