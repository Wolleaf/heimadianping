package com.hmdp.domain.dto.search;

import lombok.Data;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

/**
 * <p>
 * 
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Data
public class ShopSearchDTO {

    /**
     * 商铺名称
     */
    private String name;

    /**
     * 商铺类型的id
     */
    private Long typeId;

    /**
     * 商圈，例如陆家嘴
     */
    private String area;

    /**
     * 地址
     */
    private String address;

    /**
     * 经纬度，地理坐标点
     */
    private GeoPoint location;  // 替换原有的x,y字段

    /**
     * 最小均价，取整数
     */
    private Long minAvgPrice;

    /**
     * 最大均价，取整数
     */
    private Long maxAvgPrice;

    /**
     * 销量
     */
    private Integer sold;

    /**
     * 评论数量
     */
    private Integer comments;

    /**
     * 评分，1~5分，乘10保存，避免小数
     */
    private Integer score;

    /**
     * 营业时间，例如 10:00-22:00
     */
    private String openHours;
}
