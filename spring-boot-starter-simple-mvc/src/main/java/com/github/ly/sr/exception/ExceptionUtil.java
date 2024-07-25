package com.github.ly.sr.exception;

import com.github.ly.sr.SrConstant;

public final class ExceptionUtil {
    private ExceptionUtil() {
    }// disabled constructor

    public static void raiseException(String msg) {
        throw new BizException(SrConstant.DEFAULT_FAIL_CODE, msg);
    }

    public static void raiseException(int code, String msg) {
        throw new BizException(code, msg);
    }

    public static void raiseException(int code, String msg, Throwable throwable) {
        throw new BizException(code, msg, throwable);
    }

    public static void wrapAssert(ValidationFunction validationFunc) {
        try {
            validationFunc.validate();
        } catch (Exception e) {
            throw new DataException(SrConstant.DEFAULT_FAIL_CODE, e.getMessage(), e);
        }
    }

    public static void wrapAssert(int code, ValidationFunction validationFunc) {
        try {
            validationFunc.validate();
        } catch (Exception e) {
            throw new DataException(code, e.getMessage(), e);
        }
    }

    public static void wrapAssert(int code, Object data, ValidationFunction validationFunc) {
        try {
            validationFunc.validate();
        } catch (Exception e) {
            throw new DataException(code, e.getMessage(), e, data);
        }
    }
}
