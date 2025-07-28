package com.hmdp.domain.dto.search;

import lombok.Data;
import lombok.experimental.Accessors;

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
public class UserSearchDTO {

    /**
     * 昵称，默认是随机字符（需支持模糊搜索）
     */
    private String nickName;
}
