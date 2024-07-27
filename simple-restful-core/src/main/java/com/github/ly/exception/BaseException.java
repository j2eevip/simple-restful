package com.github.ly.exception;

import com.github.ly.constant.SrConstant;
import com.github.ly.enums.ExceptionEnum;
import lombok.Getter;

@Getter
public abstract class BaseException extends RuntimeException {
    private final int errorCode;
    private final Object errorData;

    public BaseException(String message) {
        this(SrConstant.DEFAULT_FAIL_CODE, message, null);
    }

    public BaseException(String message, Object errorData) {
        this(SrConstant.DEFAULT_FAIL_CODE, message, errorData);
    }

    public BaseException(Throwable cause) {
        super(SrConstant.DEFAULT_FAIL_MSG, cause, true, false);
        this.errorCode = SrConstant.DEFAULT_FAIL_CODE;
        this.errorData = cause;
    }

    public BaseException(ExceptionEnum exceptionEnum) {
        this(exceptionEnum.getCode(), exceptionEnum.getMessage(), null);
    }

    public BaseException(int errorCode, String message, Object errorData) {
        super(message, null, true, false);
        this.errorCode = errorCode;
        this.errorData = errorData;
    }
}
