package com.hmdp.mapper;

import com.hmdp.domain.entity.Shop;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
public interface ShopMapper extends BaseMapper<Shop> {

    /**
     * 根据id批量查询，根据id顺序排序
     * @param ids
     * @return
     */
    List<Shop> selectBatchWithOrderByField(List<Long> ids);
}
