package com.github.ly.exception;

import lombok.Getter;

@Getter
public class DataException extends BaseException {
    private final int errorCode;
    private final Object exceptionData;

    public DataException(int errorCode, String message) {
        this(errorCode, message, null, null);
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
