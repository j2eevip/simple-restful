package com.github.ly.sr.exception;


import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ResponseException {
    int code() default 500;

    String msg() default "request fail.";
    boolean isSpEl() default false;
}
