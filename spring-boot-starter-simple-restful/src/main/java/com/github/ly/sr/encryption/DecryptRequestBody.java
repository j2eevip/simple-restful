package com.github.ly.sr.encryption;

import java.lang.annotation.*;

@Inherited
@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface DecryptRequestBody {
    // 解密算法类型
    // 算法的密钥
}
