package com.hmdp.config;

import com.hmdp.domain.dto.Result;
import com.hmdp.exception.BaseException;
import com.hmdp.exception.LoginException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class WebExceptionAdvice {

    @ExceptionHandler(RuntimeException.class)
    public Result handleRuntimeException(RuntimeException e) {
        log.error(e.toString(), e);
        return Result.error("服务器异常");
    }

    @ExceptionHandler(BaseException.class)
    public Result handleBaseException(BaseException e) {
        log.error(e.toString(), e);
        return Result.error(e.getMessage());
    }

    @ExceptionHandler(LoginException.class)
    public Result handleLoginException(LoginException e) {
        log.error(e.toString(), e);
        return Result.error(e.getMessage());
    }
}
