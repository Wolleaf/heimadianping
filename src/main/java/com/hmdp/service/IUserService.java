package com.hmdp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hmdp.dto.LoginFormDTO;
import com.hmdp.entity.User;

import javax.servlet.http.HttpSession;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
public interface IUserService extends IService<User> {

    /**
     * 发送验证码
     *
     * @param phone
     */
    void sendCode(String phone);

    /**
     * 登录功能
     *
     * @param loginForm
     * @param session
     * @return
     */
    String login(LoginFormDTO loginForm, HttpSession session);

    /**
     * 登出功能
     */
    void logout();
}
