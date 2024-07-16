package com.github.ly.sr.exception;

import com.github.ly.sr.BaseResponseException;
import com.github.ly.sr.SrConstants;
import lombok.Getter;

@Getter
public class BizException extends BaseResponseException {
    private final int errorCode;

    public BizException(String message) {
        this(SrConstants.DEFAULT_ERROR_CODE, message);
    }

    public BizException(int errorCode, String message) {
        this(errorCode, message, null);
    }

    public BizException(String message, Throwable cause) {
        this(SrConstants.DEFAULT_ERROR_CODE, message, cause);
    }

    public BizException(int errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
