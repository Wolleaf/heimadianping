package com.hmdp.interceptor;

import com.hmdp.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (UserHolder.getUser() == null) {
            log.info("login拦截器拦截，用户未登录，拦截请求: {}", request.getRequestURI());
            response.setStatus(401);
            return false;
        }
        return true;
    }
}
