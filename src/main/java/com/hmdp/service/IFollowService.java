package com.hmdp.service;

import com.hmdp.domain.dto.UserDTO;
import com.hmdp.domain.entity.Follow;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
public interface IFollowService extends IService<Follow> {

    /**
     * 关注或取关
     * @param followUserId
     * @param isFollow
     */
    void follow(Long followUserId, Boolean isFollow);

    /**
     * 判断是否关注
     * @param followUserId
     * @return
     */
    Boolean isFollow(Long followUserId);

    /**
     * 共同关注
     * @param followUserId
     * @return
     */
    List<UserDTO> followCommons(Long followUserId);
}
