package com.github.ly.sr.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.ly.constant.SrConstant;
import lombok.Getter;

/**
 * 统一的结果返回值
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class SrResponseBody {
    public static SrResponseBody success() {
        return success(null, null);
    }

    public static SrResponseBody success(Object data) {
        return success(data, null);
    }

    public static SrResponseBody success(Object data, String message) {
        return new SrResponseBody(SrConstant.DEFAULT_SUCCESS_CODE, message, data);
    }

    public static SrResponseBody fail(String message) {
        return fail(SrConstant.DEFAULT_FAIL_CODE, message, null);
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
