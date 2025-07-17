package com.hmdp.config;

import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Sentinel配置类
 */
@Slf4j
@Configuration
public class SentinelConfig {

    /**
     * 配置SentinelResourceAspect
     * 用于支持@SentinelResource注解
     */
    @Bean
    public SentinelResourceAspect sentinelResourceAspect() {
        log.info("初始化SentinelResourceAspect...");
        return new SentinelResourceAspect();
    }
} 