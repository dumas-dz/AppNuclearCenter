package com.dumas.pedestal.common.util;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * 编解码工具类
 *
 * <p><b>安全警告：</b>DES 和 MD5 方法已不推荐用于新代码。
 * 新代码请使用 {@link com.dumas.pedestal.common.util.security.AESUtil} (AES-GCM) 和 SHA-256+。
 *
 * @author dumas
 */
public class CodecUtil {
    private static final Logger log = LoggerFactory.getLogger(CodecUtil.class);

    public static final String ALGORITHM_MD5 = "MD5";

    private CodecUtil() {}

    public static byte[] encodeMD5(String character, String salt) {
        try {
            if (salt != null) {
                character = character + "{" + salt + "}";
            }
            MessageDigest messageDigest = MessageDigest.getInstance(ALGORITHM_MD5);
            messageDigest.update(character.getBytes(StandardCharsets.UTF_8));
            return messageDigest.digest();
        } catch (Exception var3) {
            log.error("MD5 encode failed", var3);
            return null;
        }
    }

    public static String encodeHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    public static String encodeMD5AsHexString(String character) {
        return encodeHex(encodeMD5(character, null));
    }

    public static String encodeUrl(String character) {
        try {
            return URLEncoder.encode(character, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException var2) {
            log.error("URL encode failed", var2);
            return null;
        }
    }

    /**
     * @deprecated DES 不安全，请使用 {@link com.dumas.pedestal.common.util.security.AESUtil#encryptBytesGCM(byte[], byte[])}
     */
    @Deprecated
    public static String encodeDESAsString(String character, String secretKey) {
        try {
            return encodeDESAsString(character.getBytes(StandardCharsets.UTF_8), secretKey);
        } catch (Exception var3) {
            log.error("DES encode failed", var3);
            return null;
        }
    }

    /**
     * @deprecated DES 不安全，请使用 {@link com.dumas.pedestal.common.util.security.AESUtil#encryptBytesGCM(byte[], byte[])}
     */
    @Deprecated
    public static String encodeDESAsString(byte[] character, String secretKey) {
        return new String(Base64.encodeBase64(encodeDES(character, secretKey)));
    }

    /**
     * @deprecated DES 不安全
     */
    @Deprecated
    private static byte[] encodeDES(byte[] character, String salt) {
        try {
            SecretKey secretKey = getSaltSecretKey(salt);
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return cipher.doFinal(character);
        } catch (Exception var4) {
            log.error("DES encode failed", var4);
            return null;
        }
    }

    /**
     * @deprecated DES 不安全
     */
    @Deprecated
    private static SecretKey getSaltSecretKey(String secretKey) {
        try {
            DESKeySpec desKey = new DESKeySpec(secretKey.getBytes(StandardCharsets.UTF_8));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            return keyFactory.generateSecret(desKey);
        } catch (Exception var5) {
            log.error("DES key generation failed", var5);
            return null;
        }
    }
}
