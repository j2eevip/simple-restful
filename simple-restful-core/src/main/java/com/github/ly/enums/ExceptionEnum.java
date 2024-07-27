package com.github.ly.enums;

import lombok.Getter;

@Getter
public enum ExceptionEnum {
    SUCCESS(200, "done"),
    SYS_ERROR(500, "request error."),
    NOT_FOUND(404, "resource not found."),
    MISSING_REQUEST_PARAM_ERROR(403, "parameters error."),
    ;

    private final int code;
    private final String message;

    ExceptionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
