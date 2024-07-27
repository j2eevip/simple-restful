package com.github.ly.tools;

import com.github.ly.constant.SrConstant;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Objects;

/**
 * 加密工具类
 */
@Slf4j
public class EncryptionUtil {
    private static final String AES_ALGORITHM = "AES";
    private static final String RSA_ALGORITHM = "RSA";
    private static final String AES_TYPE = "AES/CBC/PKCS5Padding";
    private static final Base64.Decoder decoder = Base64.getDecoder();
    private static final Base64.Encoder encoder = Base64.getEncoder();
    public static final String EMPTY_STR = "";

    public static String aesEncrypt(String plainText, String privateKey) {
        return aesEncrypt(plainText.getBytes(StandardCharsets.UTF_8), privateKey.getBytes(StandardCharsets.UTF_8));
    }

    public static String aesEncrypt(byte[] dataBytes, byte[] keyBytes) {
        try {
            Cipher cipher = Cipher.getInstance(AES_TYPE);
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, AES_ALGORITHM);
            IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] encrypted = cipher.doFinal(dataBytes);
            return new String(encoder.encode(encrypted), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("AES encrypt was fail.", e);
            return EMPTY_STR;
        }
    }

    public static String aesDecrypt(String encryptData, String privateKey) {
        return aesDecrypt(encryptData.getBytes(StandardCharsets.UTF_8), privateKey.getBytes(StandardCharsets.UTF_8));
    }

    public static String aesDecrypt(byte[] encryptData, byte[] keyBytes) {
        try {
            Cipher cipher = Cipher.getInstance(AES_TYPE);
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, AES_ALGORITHM);
            IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] original = cipher.doFinal(decoder.decode(encryptData));
            return new String(original, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("AES decrypt was fail.", e);
            return EMPTY_STR;
        }
    }

    public static String encodeBase64(String plainText, String privateKey) {
        return encodeBase64(plainText.getBytes(StandardCharsets.UTF_8), privateKey.getBytes(StandardCharsets.UTF_8));
    }

    public static String encodeBase64(byte[] dataBytes, byte[] keyBytes) {
        if (Objects.isNull(dataBytes) || Objects.isNull(keyBytes) || dataBytes.length == 0 || keyBytes.length == 0) {
            log.error("base64 encode was fail, because argument is null or empty.");
            return EMPTY_STR;
        }
        byte[] encode = encoder.encode(dataBytes);
        byte[] resultBytes = new byte[keyBytes.length + encode.length];
        System.arraycopy(keyBytes, 0, resultBytes, 0, keyBytes.length);
        System.arraycopy(encode, 0, resultBytes, keyBytes.length + 1, dataBytes.length);

        return new String(resultBytes, StandardCharsets.UTF_8);
    }

    public static String decodeBase64(String plainText, String privateKey) {
        return decodeBase64(plainText.getBytes(StandardCharsets.UTF_8), privateKey);
    }

    public static String decodeBase64(byte[] dataBytes, String keyBytes) {
        if (Objects.isNull(dataBytes) || Objects.isNull(keyBytes) || dataBytes.length == 0 || keyBytes.isEmpty()) {
            log.error("base64 decode was fail, because argument is null or empty.");
            return EMPTY_STR;
        }
        byte[] decodeBytes = new byte[dataBytes.length - keyBytes.length()];
        System.arraycopy(dataBytes, keyBytes.length(), decodeBytes, 0, dataBytes.length - keyBytes.length());

        return new String(decoder.decode(decodeBytes), StandardCharsets.UTF_8);
    }

    public static String rsaDecryptByPrivate(String content, PrivateKey privateKey) {
        if (Objects.isNull(privateKey)) {
            privateKey = RSAUtil.getPrivateKey();
            if (Objects.isNull(privateKey)) {
                ExceptionUtil.raiseException(SrConstant.DEFAULT_FAIL_CODE, "privateKey is null");
            }
        }
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            //该密钥能够加密的最大字节长度
            int splitLength = ((RSAPrivateKey) privateKey).getModulus().bitLength() / 8;
            return rsaDecrypt(content, splitLength, cipher);
        } catch (Exception e) {
            log.error("RSA decrypt with public key was fail.", e);
        }
        return EMPTY_STR;
    }

    public static String rsaDecryptByPublic(String content, PublicKey publicKey) {
        if (publicKey == null) {
            publicKey = RSAUtil.getPublicKey();
            if (Objects.isNull(publicKey)) {
                ExceptionUtil.raiseException(SrConstant.DEFAULT_FAIL_CODE, "publicKey is null");
            }
        }
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            //该密钥能够加密的最大字节长度
            int splitLength = ((RSAPublicKey) publicKey).getModulus().bitLength() / 8;
            return rsaDecrypt(content, splitLength, cipher);
        } catch (Exception e) {
            log.error("RSA decrypt with public key was fail.", e);
        }
        return EMPTY_STR;
    }

    private static String rsaDecrypt(String content, int splitLength, Cipher cipher) throws Exception {
        byte[] contentBytes = RSAUtil.hexStringToBytes(content);
        byte[][] arrays = RSAUtil.splitBytes(contentBytes, splitLength);
        StringBuilder stringBuffer = new StringBuilder();
        for (byte[] array : arrays) {
            stringBuffer.append(new String(cipher.doFinal(array)));
        }
        return stringBuffer.toString();
    }

    public static String rsaEncryptByPublic(byte[] content, PublicKey publicKey) {
        if (Objects.isNull(publicKey)) {
            publicKey = RSAUtil.getPublicKey();
            if (Objects.isNull(publicKey)) {
                ExceptionUtil.raiseException(SrConstant.DEFAULT_FAIL_CODE, "publicKey is null");
            }
        }
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            //该密钥能够加密的最大字节长度
            int splitLength = ((RSAPublicKey) publicKey).getModulus().bitLength() / 8 - 11;
            return rsaEncrypt(content, splitLength, cipher);
        } catch (Exception e) {
            log.error("RSA encrypt with the public key was fail.", e);
        }
        return EMPTY_STR;
    }

    public static String rsaEncryptByPrivate(byte[] content, PrivateKey privateKey) {
        if (privateKey == null) {
            privateKey = RSAUtil.getPrivateKey();
            if (Objects.isNull(privateKey)) {
                ExceptionUtil.raiseException(SrConstant.DEFAULT_FAIL_CODE, "privateKey is null");
            }
        }
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            //该密钥能够加密的最大字节长度
            int splitLength = ((RSAPrivateKey) privateKey).getModulus().bitLength() / 8 - 11;
            return rsaEncrypt(content, splitLength, cipher);
        } catch (Exception e) {
            log.error("RSA encrypt with the private key was fail.", e);
        }
        return EMPTY_STR;
    }

    private static String rsaEncrypt(byte[] content, int splitLength, Cipher cipher) throws Exception {
        byte[][] arrays = RSAUtil.splitBytes(content, splitLength);
        StringBuilder stringBuffer = new StringBuilder();
        for (byte[] array : arrays) {
            stringBuffer.append(RSAUtil.bytesToHexString(cipher.doFinal(array)));
        }
        return stringBuffer.toString();
    }
}