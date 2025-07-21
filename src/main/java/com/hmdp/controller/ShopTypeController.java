package com.hmdp.controller;


import com.hmdp.constant.RedisConstants;
import com.hmdp.domain.dto.Result;
import com.hmdp.domain.entity.ShopType;
import com.hmdp.service.IShopTypeService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@RestController
@RequestMapping("/shop-type")
public class ShopTypeController {
    @Resource
    private IShopTypeService typeService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("list")
    public Result queryTypeList() {
        String cacheKey = RedisConstants.CACHE_SHOP_KEY + "list";
        List<ShopType> list = (List<ShopType>) redisTemplate.opsForValue().get(cacheKey);
        if (list != null) {
            return Result.success(list);
        }
        List<ShopType> typeList = typeService
                .query().orderByAsc("sort").list();
        redisTemplate.opsForValue().set(cacheKey, typeList, RedisConstants.CACHE_SHOP_TTL, TimeUnit.MINUTES);
        return Result.success(typeList);
    }
}
