package com.hmdp.controller;


import com.hmdp.convert.UserConverter;
import com.hmdp.domain.dto.LoginFormDTO;
import com.hmdp.domain.dto.Result;
import com.hmdp.domain.dto.UserDTO;
import com.hmdp.domain.dto.UserInfoDTO;
import com.hmdp.domain.entity.User;
import com.hmdp.domain.entity.UserInfo;
import com.hmdp.service.IUserInfoService;
import com.hmdp.service.IUserService;
import com.hmdp.utils.UserHolder;
import com.hmdp.valid.PhoneNumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Slf4j
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/user")
public class UserController {

    private final IUserService userService;

    private final IUserInfoService userInfoService;

    private final UserConverter userConverter;

    /**
     * 发送手机验证码
     */
    @PostMapping("/code")
    public Result sendCode(@RequestParam("phone") @PhoneNumber String phone) {
        log.info("登录手机号: {}", phone);
        userService.sendCode(phone);
        return Result.success();
    }

    /**
     * 登录功能
     * @param loginForm 登录参数，包含手机号、验证码；或者手机号、密码
     */
    @PostMapping("/login")
    public Result login(@RequestBody LoginFormDTO loginForm){
        log.info("登录参数：{}", loginForm);
        String token = userService.login(loginForm);
        return Result.success(token);
    }

    /**
     * 登出功能
     * @return 无
     */
    @PostMapping("/logout")
    public Result logout(){
        userService.logout();
        return Result.success();
    }

    @GetMapping("/me")
    public Result me(){
        return Result.success(UserHolder.getUser());
    }

    @GetMapping("/info/{id}")
    public Result info(@PathVariable("id") @NotNull(message = "用户ID不能为空") Long userId){
        // 查询详情
        UserInfo info = userInfoService.getById(userId);
        if (info == null) {
            // 没有详情，应该是第一次查看详情
            return Result.success();
        }
        UserInfoDTO userInfoDTO = userConverter.userInfo2UserInfoDTO(info);
        // 返回
        return Result.success(userInfoDTO);
    }

    @GetMapping("/{id}")
    public Result getUserById(@PathVariable("id") @NotNull(message = "用户id不能为空") Long id) {
        User user = userService.getById(id);
        UserDTO userDTO = userConverter.user2UserDTO(user);
        return Result.success(userDTO);
    }

    @PostMapping("/sign")
    public Result sign() {
        userService.sign();
        return Result.success();
    }

    @GetMapping("/sign/count")
    public Result signCount() {
        Integer count = userService.signCount();
        return Result.success(count);
    }
}
