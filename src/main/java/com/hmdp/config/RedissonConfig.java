package com.hmdp.config;

import com.hmdp.properties.RedisProperties;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient redissonClient(RedisProperties redisProperties){
        String address = "redis://" + redisProperties.getHost() + ":" + redisProperties.getPort();
        Config config = new Config();
        config.useSingleServer().setAddress(address).setPassword(redisProperties.getPassword());
        return Redisson.create(config);
    }
}
