package com.hmdp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.constant.MessageConstants;
import com.hmdp.constant.RedisConstants;
import com.hmdp.entity.Shop;
import com.hmdp.exception.ShopException;
import com.hmdp.mapper.ShopMapper;
import com.hmdp.service.IShopService;
import com.hmdp.utils.CacheClient;
import com.hmdp.utils.RedisData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {

    private final StringRedisTemplate stringRedisTemplate;
    private final CacheClient cacheClient;

    @Override
    public Shop queryById(Long id) {
        String key = RedisConstants.CACHE_SHOP_KEY + id;
        Shop shop = cacheClient.getWithCachePenetration(
                key, Shop.class, id, this::getById, RedisConstants.CACHE_SHOP_TTL, TimeUnit.MINUTES);
        if (shop == null) {
            throw new ShopException(MessageConstants.SHOP_NOT_FOUND);
        }
        return shop;
    }

    @Override
    @Transactional
    public void updateByIdCache(Shop shop) {
        Long id = shop.getId();
        if (id == null) {
            throw new ShopException(MessageConstants.SHOP_NOT_FOUND);
        }
        updateById(shop);
        String cacheKey = RedisConstants.CACHE_SHOP_KEY + shop.getId();
        stringRedisTemplate.delete(cacheKey);
    }
}
