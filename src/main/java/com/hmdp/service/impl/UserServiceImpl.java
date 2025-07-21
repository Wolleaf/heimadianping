package com.hmdp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.constant.LoginConstants;
import com.hmdp.constant.RedisConstants;
import com.hmdp.constant.SystemConstants;
import com.hmdp.domain.dto.LoginFormDTO;
import com.hmdp.domain.dto.UserDTO;
import com.hmdp.domain.entity.User;
import com.hmdp.exception.LoginException;
import com.hmdp.mapper.UserMapper;
import com.hmdp.service.IUserService;
import com.hmdp.constant.MessageConstants;
import com.hmdp.utils.RegexUtils;
import com.hmdp.utils.UserHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final HttpServletRequest request;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void sendCode(String phone) {
        // 验证手机号
        if (RegexUtils.isPhoneInvalid(phone)) {
            throw new LoginException(MessageConstants.INVALID_PHONE);
        }
        // 生成验证码
        String code = RandomUtil.randomNumbers(6);
        // 保存验证码
        redisTemplate.opsForValue().set(RedisConstants.LOGIN_CODE_KEY + phone,
                code, RedisConstants.LOGIN_CODE_TTL, TimeUnit.MINUTES);
        // 发送验证码
        System.out.println("验证码：" + code);
    }

    @Override
    public String login(LoginFormDTO loginForm, HttpSession session) {
        // 验证手机号
        String phone = loginForm.getPhone();
        if (RegexUtils.isPhoneInvalid(phone)) {
            throw new LoginException(MessageConstants.INVALID_PHONE);
        }
        // 校验验证码
        String code = (String) redisTemplate.opsForValue().get(RedisConstants.LOGIN_CODE_KEY + phone);
        if (code == null || !code.equals(loginForm.getCode())) {
            throw new LoginException(MessageConstants.INVALID_CODE);
        }
        // 根据手机号查询用户，用户是否存在
        User user = lambdaQuery().eq(User::getPhone, phone).one();
        if (user == null) {
            user = User.builder()
                    .nickName(SystemConstants.USER_NICK_NAME_PREFIX + RandomUtil.randomString(10))
                    .phone(phone)
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build();
            save(user);
        }
        // 保存用户信息到redis
        String token = UUID.randomUUID().toString(true);
        String tokenKey = RedisConstants.LOGIN_USER_KEY + token;
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        Map<String, Object> map = BeanUtil.beanToMap(userDTO);
        redisTemplate.opsForHash().putAll(tokenKey, map);
        // 设置token有效期
        redisTemplate.expire(tokenKey, RedisConstants.LOGIN_USER_TTL, TimeUnit.MINUTES);
        return token;
    }

    @Override
    public void logout() {
        String token = request.getHeader(LoginConstants.TOKEN_NAME);
        String key = RedisConstants.LOGIN_USER_KEY + token;
        stringRedisTemplate.delete(key);
    }

    @Override
    public void sign() {
        LocalDateTime now = LocalDateTime.now();
        String key = RedisConstants.USER_SIGN_KEY + UserHolder.getUser().getId() + ":" +
                now.format(DateTimeFormatter.ofPattern("yyyyMM"));
        int dayOfMonth = now.getDayOfMonth();
        stringRedisTemplate.opsForValue().setBit(key, dayOfMonth - 1, true);
    }

    @Override
    public Integer signCount() {
        LocalDateTime now = LocalDateTime.now();
        String key = RedisConstants.USER_SIGN_KEY + UserHolder.getUser().getId() + ":" +
                now.format(DateTimeFormatter.ofPattern("yyyyMM"));
        int dayOfMonth = now.getDayOfMonth();
        List<Long> longs = stringRedisTemplate.opsForValue().bitField(
                key,
                BitFieldSubCommands.create().get(BitFieldSubCommands.BitFieldType.unsigned(dayOfMonth)).valueAt(0)
        );
        if (longs == null) {
            return null;
        }
        long value = longs.get(0);
        int cnt = 0;
        while ((value & 1) == 1) {
            cnt++;
            value = value >>> 1;
        }
        return cnt;
    }
}
