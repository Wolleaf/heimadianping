package com.hmdp;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.json.JSONUtil;
import com.hmdp.constant.RedisConstants;
import com.hmdp.dto.UserDTO;
import com.hmdp.entity.Shop;
import com.hmdp.entity.User;
import com.hmdp.service.IShopService;
import com.hmdp.service.IUserService;
import com.hmdp.utils.CacheClient;
import com.hmdp.utils.RedisIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    @Resource
    private IUserService userService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

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

    @Test
    public void allUserLogin() {
        List<User> userList = userService.list();
        try (
                FileWriter fw = new FileWriter("E:\\Project\\hm-dianping\\src\\main\\resources\\token.txt");
        ) {
            StringBuilder sb = new StringBuilder();
            for (User user : userList) {
                // 保存用户信息到redis
                String token = UUID.randomUUID().toString(true);
                sb.append(token).append("\n");
                String tokenKey = RedisConstants.LOGIN_USER_KEY + token;
                UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
                Map<String, Object> map = BeanUtil.beanToMap(userDTO);
                redisTemplate.opsForHash().putAll(tokenKey, map);
            }
            fw.write(sb.toString());
        } catch (Exception e) {
            log.error("写入token.txt失败", e);
        }
    }

    @Test
    public void importGeo() {
        List<Shop> shops = shopService.list();
        Map<Long, List<Shop>> map = shops.stream().collect(Collectors.groupingBy(Shop::getTypeId));
        for (Map.Entry<Long, List<Shop>> entry : map.entrySet()) {
            Long typeId = entry.getKey();
            List<Shop> value = entry.getValue();
            String key = RedisConstants.SHOP_GEO_KEY + typeId;
            List<RedisGeoCommands.GeoLocation<String>> list = new ArrayList<>(value.size());
            for (Shop shop : value) {
                RedisGeoCommands.GeoLocation<String> location =
                        new RedisGeoCommands.GeoLocation<>(shop.getId().toString(), new Point(shop.getX(), shop.getY()));
                list.add(location);
            }
            stringRedisTemplate.opsForGeo().add(key, list);
        }
    }
}
