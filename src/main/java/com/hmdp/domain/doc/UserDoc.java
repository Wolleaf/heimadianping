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
@Document(indexName = "hmdp_user")
public class UserDoc {

    /**
     * 主键
     */
    @Id
    private Long id;

    /**
     * 昵称，默认是随机字符（需支持模糊搜索）
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String nickName;

    /**
     * 用户头像（仅存储不索引）
     */
    @Field(type = FieldType.Keyword, index = false)
    private String icon = "";
}
