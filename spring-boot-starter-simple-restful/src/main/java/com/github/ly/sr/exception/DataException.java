package com.github.ly.sr.exception;

import com.github.ly.sr.BaseResponseException;
import com.github.ly.sr.SrConstants;
import lombok.Getter;

@Getter
public class DataException extends BaseResponseException {
    private final int errorCode;
    private final Object exceptionData;

    public DataException(String message) {
        this(SrConstants.DEFAULT_ERROR_CODE, message);
    }
    public DataException(int errorCode, String message) {
        this(errorCode, message, null, null);
    }

    public DataException(String message, Throwable cause) {
        this(SrConstants.DEFAULT_ERROR_CODE, message, cause, null);
    }

    public DataException(int errorCode, String message, Throwable cause) {
        this(errorCode, message, cause, null);
    }

    public DataException(int errorCode, String message, Throwable cause, Object exceptionData) {
        super(message, cause);
        this.errorCode = errorCode;
        this.exceptionData = exceptionData;
    }
}
