package com.github.ly.sr.privacy;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.ly.enums.PrivacyTypeEnum;

import java.lang.annotation.*;

@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@JacksonAnnotationsInside
@JsonSerialize(using = PrivacySerialize.class)
public @interface PrivacyColumn {
    /**
     * 脱敏数据类型, 非Customer时, 将忽略 refixNoMaskLen 和 suffixNoMaskLen 和 maskStr
     */
    PrivacyTypeEnum type() default PrivacyTypeEnum.CUSTOMER;

    /**
     * 前置不需要打码的长度
     */
    int prefixNoMaskLen() default 0;

    /**
     * 后置不需要打码的长度
     */
    int suffixNoMaskLen() default 0;

    /**
     * 用什么打码
     */
    String maskStr() default "*";
}
