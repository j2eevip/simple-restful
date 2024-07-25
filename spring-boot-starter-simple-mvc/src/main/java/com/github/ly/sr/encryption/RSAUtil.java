package com.github.ly.sr.encryption;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

@Slf4j
public class RSAUtil {
    /**
     * 算法名称
     */
    private static final String ALGORITHM = "RSA";
    /**
     * 默认密钥大小
     */
    private static final int KEY_SIZE = 1024;
    /**
     * 用来指定保存密钥对的文件名和存储的名称
     */
    private static final String PUBLIC_KEY_NAME = "publicKey";
    private static final String PRIVATE_KEY_NAME = "privateKey";
    private static final String PUBLIC_FILENAME = "publicKey.properties";
    private static final String PRIVATE_FILENAME = "privateKey.properties";
    /**
     * 密钥对生成器
     */
    private static
    KeyPairGenerator keyPairGenerator = null;

    private static KeyFactory keyFactory = null;
    /**
     * 缓存的密钥对
     */
    private static KeyPair keyPair = null;
    /**
     * Base64 编码/解码器 JDK1.8
     */
    private static final Base64.Decoder decoder = Base64.getDecoder();
    private static final Base64.Encoder encoder = Base64.getEncoder();

    /** 初始化密钥工厂 */
    static {
        try {
            keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
            keyFactory = KeyFactory.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            log.error("RSAUtil[ERROR]:e", e);
        }
    }

    /**
     * 私有构造器
     */
    private RSAUtil() {
    }

    /**
     * 生成密钥对
     * 将密钥分别用Base64编码保存到#publicKey.properties#和#privateKey.properties#文件中
     * 保存的默认名称分别为publicKey和privateKey
     */
    public static synchronized Map<String, Object> generateKeyPair() {
        try {
            keyPairGenerator.initialize(KEY_SIZE, new SecureRandom(UUID.randomUUID().toString().replaceAll("-", "").getBytes()));
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (InvalidParameterException e) {
            log.error("KeyPairGenerator does not support a key length of " + KEY_SIZE + ".", e);
        } catch (NullPointerException e) {
            log.error("RSAUtil#key_pair_gen is null,can not generate KeyPairGenerator instance.", e);
        }
        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
        String publicKeyString = encoder.encodeToString(rsaPublicKey.getEncoded());
        String privateKeyString = encoder.encodeToString(rsaPrivateKey.getEncoded());
        storeKey(publicKeyString, PUBLIC_KEY_NAME, PUBLIC_FILENAME);
        storeKey(privateKeyString, PRIVATE_KEY_NAME, PRIVATE_FILENAME);
        Map<String, Object> keyPair = new HashMap<>();
        keyPair.put("public", publicKeyString);
        keyPair.put("private", privateKeyString);
        return keyPair;
    }

    /**
     * 将指定的密钥字符串保存到文件中,如果找不到文件，就创建
     *
     * @param keyString 密钥的Base64编码字符串（值）
     * @param keyName   保存在文件中的名称（键）
     * @param fileName  目标文件名
     */
    private static void storeKey(String keyString, String keyName, String fileName) {
        Properties properties = new Properties();
        //存放密钥的绝对地址
        String path = null;
        try {
            path = RSAUtil.class.getClassLoader().getResource(fileName).toString();
            path = path.substring(path.indexOf(":") + 1);
        } catch (NullPointerException e) {
            //如果不存#fileName#就创建
            log.warn("storeKey()# " + fileName + " is not exist.Begin to create this file.");
            String classPath = RSAUtil.class.getClassLoader().getResource("").toString();
            String prefix = classPath.substring(classPath.indexOf(":") + 1);
            String suffix = fileName;
            File file = new File(prefix + suffix);
            try {
                file.createNewFile();
                path = file.getAbsolutePath();
            } catch (IOException e1) {
                log.error(fileName + " create fail.", e1);
            }
        }
        try (OutputStream out = new FileOutputStream(path)) {
            properties.setProperty(keyName, keyString);
            properties.store(out, "There is " + keyName);
        } catch (FileNotFoundException e) {
            log.error("ModulusAndExponent.properties is not found.", e);
        } catch (IOException e) {
            log.error("OutputStream output failed.", e);
        }
    }

    /**
     * 获取密钥字符串
     *
     * @param keyName  需要获取的密钥名
     * @param fileName 密钥所在文件
     * @return Base64编码的密钥字符串
     */
    private static String getKeyString(String keyName, String fileName) {
        if (RSAUtil.class.getClassLoader().getResource(fileName) == null) {
            log.warn("getKeyString()# " + fileName + " is not exist.Will run #generateKeyPair()# firstly.");
            generateKeyPair();
        }
        try (InputStream in = RSAUtil.class.getClassLoader().getResource(fileName).openStream()) {
            Properties properties = new Properties();
            properties.load(in);
            return properties.getProperty(keyName);
        } catch (IOException e) {
            log.error("getKeyString()#" + e.getMessage(), e);
        }
        return null;
    }

    /**
     * 从文件获取RSA公钥
     *
     * @return RSA公钥
     * @throws InvalidKeySpecException
     */
    public static RSAPublicKey getPublicKey() {
        try {
            byte[] keyBytes = decoder.decode(getKeyString(PUBLIC_KEY_NAME, PUBLIC_FILENAME));
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
            return (RSAPublicKey) keyFactory.generatePublic(x509EncodedKeySpec);
        } catch (InvalidKeySpecException e) {
            log.error("getPublicKey()#" + e.getMessage(), e);
        }
        return null;
    }

    public static RSAPublicKey getPublicKey(String publicKeyString) {
        if (StringUtils.isBlank(publicKeyString)) {
            return null;
        }
        try {
            byte[] keyBytes = decoder.decode(publicKeyString);
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
            return (RSAPublicKey) keyFactory.generatePublic(x509EncodedKeySpec);
        } catch (InvalidKeySpecException e) {
            log.error("getPublicKey()#" + e.getMessage(), e);
        }
        return null;
    }

    /**
     * 从文件获取RSA私钥
     *
     * @return RSA私钥
     * @throws InvalidKeySpecException
     */
    public static RSAPrivateKey getPrivateKey() {
        try {
            byte[] keyBytes = decoder.decode(getKeyString(PRIVATE_KEY_NAME, PRIVATE_FILENAME));
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
            return (RSAPrivateKey) keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        } catch (InvalidKeySpecException e) {
            log.error("getPrivateKey()#" + e.getMessage(), e);
        }
        return null;
    }

    public static RSAPrivateKey getPrivateKey(String privateKeyString) {
        if (StringUtils.isBlank(privateKeyString)) {
            return null;
        }
        try {
            byte[] keyBytes = decoder.decode(privateKeyString);
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
            return (RSAPrivateKey) keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        } catch (InvalidKeySpecException e) {
            log.error("getPrivateKey()#" + e.getMessage(), e);
        }
        return null;
    }

    /**
     * 根据限定的每组字节长度，将字节数组分组
     *
     * @param bytes       等待分组的字节组
     * @param splitLength 每组长度
     * @return 分组后的字节组
     */
    public static byte[][] splitBytes(byte[] bytes, int splitLength) {
        //bytes与splitLength的余数
        int remainder = bytes.length % splitLength;
        //数据拆分后的组数，余数不为0时加1
        int quotient = remainder != 0 ? bytes.length / splitLength + 1 : bytes.length / splitLength;
        byte[][] arrays = new byte[quotient][];
        byte[] array = null;
        for (int i = 0; i < quotient; i++) {
            //如果是最后一组（quotient-1）,同时余数不等于0，就将最后一组设置为remainder的长度
            if (i == quotient - 1 && remainder != 0) {
                array = new byte[remainder];
                System.arraycopy(bytes, i * splitLength, array, 0, remainder);
            } else {
                array = new byte[splitLength];
                System.arraycopy(bytes, i * splitLength, array, 0, splitLength);
            }
            arrays[i] = array;
        }
        return arrays;
    }

    /**
     * 将字节数组转换成16进制字符串
     *
     * @param bytes 即将转换的数据
     * @return 16进制字符串
     */
    public static String bytesToHexString(byte[] bytes) {
        StringBuffer sb = new StringBuffer(bytes.length);
        String temp = null;
        for (int i = 0; i < bytes.length; i++) {
            temp = Integer.toHexString(0xFF & bytes[i]);
            if (temp.length() < 2) {
                sb.append(0);
            }
            sb.append(temp);
        }
        return sb.toString();
    }

    /**
     * 将16进制字符串转换成字节数组
     *
     * @param hex 16进制字符串
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hex) {
        int len = (hex.length() / 2);
        hex = hex.toUpperCase();
        byte[] result = new byte[len];
        char[] chars = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(chars[pos]) << 4 | toByte(chars[pos + 1]));
        }
        return result;
    }

    /**
     * 将char转换为byte
     *
     * @param c char
     * @return byte
     */
    private static byte toByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }
}
