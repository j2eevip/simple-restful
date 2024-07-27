package com.github.ly.exception;

import lombok.Getter;

@Getter
public class BizException extends BaseException {
    private final int errorCode;

    public BizException(int errorCode, String message) {
        this(errorCode, message, null);
    }

    public BizException(int errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
