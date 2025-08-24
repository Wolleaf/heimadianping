package com.hmdp.controller;


import com.hmdp.domain.dto.Result;
import com.hmdp.domain.dto.UserDTO;
import com.hmdp.service.IFollowService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/follow")
public class FollowController {

    private final IFollowService followService;

    /**
     * 关注，取消关注
     * @param followUserId 关注的用户id
     * @param isFollow 是否关注
     * @return 响应结果
     */
    @ApiOperation("关注或取消关注")
    @PutMapping("/{id}/{idFollow}")
    public Result follow(@PathVariable("id") Long followUserId, @PathVariable("idFollow") Boolean isFollow) {
        followService.follow(followUserId, isFollow);
        return Result.success();
    }

    @GetMapping("/or/not/{id}")
    public Result followOrNot(@PathVariable("id") Long followUserId) {
        Boolean isFollow = followService.isFollow(followUserId);
        return Result.success(isFollow);
    }

    @GetMapping("/common/{id}")
    public Result followCommons(@PathVariable Long id) {
        List<UserDTO> users = followService.followCommons(id);
        return Result.success(users);
    }
}
