package com.github.ly.sr;

import lombok.Getter;

/**
 * 统一的结果返回值
 */
@Getter
public final class SrResponseBody {
    public static SrResponseBody success(int successCode) {
        return success(successCode, null);
    }

    public static SrResponseBody success(int successCode, Object data) {
        return new SrResponseBody(successCode, "success", data);
    }

    public static SrResponseBody fail(int code, String message) {
        return fail(code, message, null);
    }

    public static SrResponseBody fail(int code, String message, Object data) {
        return new SrResponseBody(code, message, data);
    }

    private SrResponseBody(final int code, final String msg, final Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    private final Integer code;
    private final String msg;
    private final Object data;
}
