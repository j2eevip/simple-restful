package com.github.ly.sr.exception;


import com.github.ly.sr.SrConstant;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ResponseException {
    int code() default SrConstant.DEFAULT_FAIL_CODE;

    String msg() default SrConstant.DEFAULT_FAIL_MSG;
    boolean isSpEl() default false;
}
