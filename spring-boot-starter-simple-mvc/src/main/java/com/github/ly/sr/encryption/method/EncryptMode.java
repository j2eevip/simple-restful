package com.github.ly.sr.encryption.method;

import com.github.ly.sr.encryption.EncryptionUtil;
import com.github.ly.sr.encryption.RSAUtil;

import java.nio.charset.StandardCharsets;
import java.security.interfaces.RSAPrivateKey;
import java.util.function.BiFunction;

public enum EncryptMode {
    /**
     * BASE64
     */
    BASE64(EncryptionUtil::encodeBase64, EncryptionUtil::decodeBase64),

    /**
     * AES
     */
    AES(EncryptionUtil::aesEncrypt, EncryptionUtil::aesDecrypt),

    /**
     * RSA
     */
    RSA((content, key) -> {
        RSAPrivateKey privateKey = RSAUtil.getPrivateKey(key);
        return EncryptionUtil.rsaEncryptByPrivate(content.getBytes(StandardCharsets.UTF_8), privateKey);
    }, (content, key) -> {
        RSAPrivateKey privateKey = RSAUtil.getPrivateKey(key);
        return EncryptionUtil.rsaDecryptByPrivate(content, privateKey);
    });

    private final BiFunction<String, String, String> encryptFunc;
    private final BiFunction<String, String, String> decryptFunc;

    EncryptMode(BiFunction<String, String, String> encryptFunc, BiFunction<String, String, String> decryptFunc) {
        this.encryptFunc = encryptFunc;
        this.decryptFunc = decryptFunc;
    }

    public String getDecryptStr(String content, String key) {
        return decryptFunc.apply(content, key);
    }

    public String getEncryptStr(String content, String key) {
        return encryptFunc.apply(content, key);
    }
}
