package com.hmdp.domain.doc;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

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
@Document(indexName = "hmdp_blog")
public class BlogDoc {

    /**
     * 主键
     */
    @Id
    private Long id;

    /**
     * 商户id
     */
    @Field(type = FieldType.Long)
    private Long shopId;

    /**
     * 用户id
     */
    @Field(type = FieldType.Long)
    private Long userId;

    /**
     * 用户图标
     */
    @Field(type = FieldType.Keyword, index = false) // 不索引图片URL（仅存储）
    private String icon;

    /**
     * 用户姓名
     */
    @Field(type = FieldType.Keyword) // 姓名精确匹配（不分词）
    private String name;

    /**
     * 是否点赞过了
     */
    @Field(type = FieldType.Boolean) // 布尔类型直接映射
    private Boolean isLike;

    /**
     * 标题
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word") // 标题需中文分词
    private String title;

    /**
     * 探店的照片，最多9张，多张以","隔开
     */
    @Field(type = FieldType.Text, index = false) // 不索引图片URL列表
    private String images;

    /**
     * 探店的文字描述
     */
    @Field(type = FieldType.Text, analyzer = "ik_smart") // 内容用粗粒度分词
    private String content;

    /**
     * 点赞数量
     */
    @Field(type = FieldType.Integer) // 数值类型
    private Integer liked;

    /**
     * 评论数量
     */
    @Field(type = FieldType.Integer)
    private Integer comments;
}
