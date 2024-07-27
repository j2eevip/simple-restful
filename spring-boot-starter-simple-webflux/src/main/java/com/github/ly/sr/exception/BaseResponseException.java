package com.github.ly.sr.exception;

public abstract class BaseResponseException extends RuntimeException {
    public BaseResponseException(String message) {
        super(message);
    }

    public BaseResponseException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseResponseException(Throwable cause) {
        super(cause);

    }

    public BaseResponseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public abstract int getErrorCode();

    public Object exceptionData() {
        return null;
    }
}
