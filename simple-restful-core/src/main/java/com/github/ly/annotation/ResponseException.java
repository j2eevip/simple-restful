package com.github.ly.annotation;

import com.github.ly.constant.SrConstant;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ResponseException {
    int code() default SrConstant.DEFAULT_FAIL_CODE;

    String msg() default SrConstant.DEFAULT_FAIL_MSG;

    boolean isSpEl() default false;
}
