package com.github.ly.sr.exception;

import lombok.Getter;

@Getter
public class BizException extends BaseResponseException {
    private final int errorCode;

    public BizException(int errorCode, String message) {
        this(errorCode, message, null);
    }

    public BizException(int errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
