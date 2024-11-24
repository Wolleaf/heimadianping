package com.hmdp.interceptor;

import cn.hutool.core.bean.BeanUtil;
import com.hmdp.constant.LoginConstants;
import com.hmdp.constant.RedisConstants;
import com.hmdp.dto.UserDTO;
import com.hmdp.utils.UserHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RefreshTokenInterceptor implements HandlerInterceptor {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求头中的token
        String token = request.getHeader(LoginConstants.TOKEN_NAME);
        if (token == null) {
            return true;
        }
        String tokenKey = RedisConstants.LOGIN_USER_KEY + token;
        // 获取Redis中的用户信息
        Map<Object, Object> map = redisTemplate.opsForHash().entries(tokenKey);
        if (map.isEmpty()) {
            return true;
        }
        // 刷新token持续时间
        redisTemplate.expire(tokenKey, RedisConstants.LOGIN_USER_TTL, TimeUnit.MINUTES);
        // 将用户信息保存到ThreadLocal中
        UserDTO user = new UserDTO();
        BeanUtil.fillBeanWithMap(map, user, false);
        UserHolder.saveUser(user);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.removeUser();
    }
}
