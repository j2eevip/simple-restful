package com.github.ly.sr.encryption.method;

import java.lang.annotation.*;

@Inherited
@Documented
@Target({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Decrypt {
    // 解密算法类型
    EncryptMode mode() default EncryptMode.BASE64;
    // 算法的密钥
}
