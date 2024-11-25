package com.hmdp.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.constant.MessageConstants;
import com.hmdp.constant.RedisConstants;
import com.hmdp.constant.SystemConstants;
import com.hmdp.entity.Shop;
import com.hmdp.exception.ShopException;
import com.hmdp.mapper.ShopMapper;
import com.hmdp.service.IShopService;
import com.hmdp.utils.CacheClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.domain.geo.GeoReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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

    @Override
    public List<Shop> queryShopByType(Integer typeId, Integer current, Double x, Double y) {
        // 判断查询类型
        if (x == null || y == null) {
            // 根据类型分页查询
            Page<Shop> page = query()
                    .eq("type_id", typeId)
                    .page(new Page<>(current, SystemConstants.DEFAULT_PAGE_SIZE));
            return page.getRecords();
        }
        // 设置查询参数
        String key = RedisConstants.SHOP_GEO_KEY + typeId;
        int from = (current - 1) * SystemConstants.DEFAULT_PAGE_SIZE;
        int end = current * SystemConstants.DEFAULT_PAGE_SIZE;
        // 开始查询
        GeoResults<RedisGeoCommands.GeoLocation<String>> search = stringRedisTemplate.opsForGeo().search(
                key,
                GeoReference.fromCoordinate(x, y),
                new Distance(5000),
                RedisGeoCommands.GeoSearchCommandArgs.newGeoSearchArgs().includeDistance().limit(end)
        );
        // 处理封装数据
        if (search == null) return Collections.emptyList();
        List<GeoResult<RedisGeoCommands.GeoLocation<String>>> content = search.getContent();
        if (content.size() <= from) return Collections.emptyList();
        List<Long> shopIds = new ArrayList<>(content.size());
        Map<Long, Double> distanceMap = new HashMap<>(content.size());
        content.stream().skip(from).forEach(geoLocationGeoResult -> {
            Long shopId = Long.valueOf(geoLocationGeoResult.getContent().getName());
            shopIds.add(shopId);
            distanceMap.put(shopId, geoLocationGeoResult.getDistance().getValue());
        });
        List<Shop> shops = baseMapper.selectBatchWithOrderByField(shopIds);
        shops.forEach(shop -> shop.setDistance(distanceMap.get(shop.getId())));
        return shops;
    }
}
