package com.hmdp.mapper;

import com.hmdp.domain.entity.User;
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
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据id批量查询用户信息
     * @param ids
     * @return
     */
    List<User> listUserByIdsWithOrder(List<Long> ids);
}
