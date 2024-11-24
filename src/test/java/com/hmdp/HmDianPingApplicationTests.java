package com.hmdp;

import cn.hutool.json.JSONUtil;
import com.hmdp.entity.Shop;
import com.hmdp.service.IShopService;
import com.hmdp.utils.CacheClient;
import com.hmdp.utils.RedisIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Slf4j
@SpringBootTest
class HmDianPingApplicationTests {

    @Resource
    private IShopService shopService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private CacheClient cacheClient;
    @Resource
    private RedisIdGenerator redisIdGenerator;

    @Test
    public void testSaveShop() throws InterruptedException {
        String cacheKey = "cache:shop:1";
        Shop temp = shopService.getById(1);
        cacheClient.setWithLogicExpire(cacheKey, temp, 20L, TimeUnit.SECONDS);
    }

    @Test
    public void testJSONUtil() {
        log.info("null: {}", JSONUtil.toJsonStr(""));
    }

    @Test
    public void testRedisIdGenerator() {
        System.out.println(redisIdGenerator.nextId("order"));
    }

}
