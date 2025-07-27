package com.hmdp.domain.doc;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.GeoPointField;
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
@Accessors(chain = true)
@Document(indexName = "hmdp_shop")
public class ShopDoc {

    /**
     * 主键
     */
    @Id
    private Long id;

    /**
     * 商铺名称
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word")  // 商铺名称需中文分词
    private String name;

    /**
     * 商铺类型的id
     */
    @Field(type = FieldType.Long)
    private Long typeId;

    /**
     * 商铺图片，多个图片以','隔开
     */
    @Field(type = FieldType.Keyword, index = false)  // 图片URL不参与索引
    private String images;

    /**
     * 商圈，例如陆家嘴
     */
    @Field(type = FieldType.Keyword)  // 商圈名称精确匹配（如“陆家嘴”）
    private String area;

    /**
     * 地址
     */
    @Field(type = FieldType.Text, analyzer = "ik_smart")  // 地址需分词但粒度较粗
    private String address;

    /**
     * 经纬度，地理坐标点
     */
    @GeoPointField  // 标记为地理坐标点类型
    private GeoPoint location;  // 替换原有的x,y字段

    /**
     * 均价，取整数
     */
    @Field(type = FieldType.Long)
    private Long avgPrice;

    /**
     * 销量
     */
    @Field(type = FieldType.Integer)  // 支持聚合统计
    private Integer sold;

    /**
     * 评论数量
     */
    @Field(type = FieldType.Integer)
    private Integer comments;

    /**
     * 评分，1~5分，乘10保存，避免小数
     */
    @Field(type = FieldType.Integer)
    private Integer score;

    /**
     * 营业时间，例如 10:00-22:00
     */
    @Field(type = FieldType.Keyword, index = false)
    private String openHours;
}
