package com.github.ly.sr.annotation;


import com.github.ly.sr.SrConstants;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ResponseException {
    int code() default SrConstants.DEFAULT_ERROR_CODE;
    String msg() default SrConstants.DEFAULT_ERROR_MSG;
    boolean isSpEl() default false;
}
