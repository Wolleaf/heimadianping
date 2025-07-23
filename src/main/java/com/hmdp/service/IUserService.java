package com.hmdp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hmdp.domain.dto.LoginFormDTO;
import com.hmdp.domain.entity.User;

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
     * @return
     */
    String login(LoginFormDTO loginForm);

    /**
     * 登出功能
     */
    void logout();

    /**
     * 签到功能
     */
    void sign();

    /**
     * 统计连续签到天数
     * @return
     */
    Integer signCount();
}
