package com.hmdp.exception;

/**
 * 统一异常
 */
public class BaseException extends RuntimeException {

    public BaseException() {

    }

    public BaseException(String message) {
        super(message);
    }
}
