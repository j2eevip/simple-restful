package com.github.ly.annotation;

import com.github.ly.enums.EncryptMode;

import java.lang.annotation.*;

@Inherited
@Documented
@Target({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Decrypt {
    EncryptMode mode() default EncryptMode.BASE64;
}
