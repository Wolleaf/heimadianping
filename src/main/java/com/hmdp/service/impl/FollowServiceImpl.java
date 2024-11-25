package com.hmdp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hmdp.constant.RedisConstants;
import com.hmdp.dto.UserDTO;
import com.hmdp.entity.Follow;
import com.hmdp.mapper.FollowMapper;
import com.hmdp.service.IFollowService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.service.IUserService;
import com.hmdp.utils.UserHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
@RequiredArgsConstructor
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements IFollowService {

    private final StringRedisTemplate stringRedisTemplate;
    private final IUserService userService;

    /**
     * 关注或取关
     * @param followUserId
     * @param isFollow
     */
    @Override
    @Transactional
    public void follow(Long followUserId, Boolean isFollow) {
        String key = RedisConstants.FOLLOW_USER_KEY + UserHolder.getUser().getId();
        if (isFollow) {
            Follow follow = Follow.builder()
                    .userId(UserHolder.getUser().getId())
                    .followUserId(followUserId)
                    .createTime(LocalDateTime.now())
                    .build();
            int isSuccess = baseMapper.insert(follow);
            if (isSuccess > 0) {
                stringRedisTemplate.opsForSet().add(key, followUserId.toString());
            }
        }
        else {
            LambdaQueryWrapper<Follow> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Follow::getUserId, UserHolder.getUser().getId())
                    .eq(Follow::getFollowUserId, followUserId);
            baseMapper.delete(queryWrapper);
            Long isSuccess = stringRedisTemplate.opsForSet().remove(key, followUserId.toString());
            if (isSuccess != null && isSuccess > 0) {
                stringRedisTemplate.opsForSet().add(key, followUserId.toString());
            }
        }
    }

    /**
     * 判断是否关注
     * @param followUserId
     * @return
     */
    @Override
    public Boolean isFollow(Long followUserId) {
        Integer count = lambdaQuery()
                .eq(Follow::getUserId, UserHolder.getUser().getId())
                .eq(Follow::getFollowUserId, followUserId)
                .count();
        return count > 0;
    }

    /**
     * 共同关注
     * @param followUserId
     * @return
     */
    @Override
    public List<UserDTO> followCommons(Long followUserId) {
        String userKey = RedisConstants.FOLLOW_USER_KEY + UserHolder.getUser().getId();
        String followUserKey = RedisConstants.FOLLOW_USER_KEY + followUserId;
        Set<String> intersect = stringRedisTemplate.opsForSet().intersect(userKey, followUserKey);
        if (intersect == null || intersect.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> ids = intersect.stream().map(Long::valueOf).collect(Collectors.toList());
        List<UserDTO> userDTOs = userService.listByIds(ids).stream()
                .map(user -> BeanUtil.copyProperties(user, UserDTO.class))
                .collect(Collectors.toList());
        return userDTOs;
    }
}
