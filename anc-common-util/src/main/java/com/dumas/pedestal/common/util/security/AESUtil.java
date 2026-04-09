package com.dumas.pedestal.common.util.security;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dumas.pedestal.common.util.Base64Util;
import io.netty.buffer.ByteBufUtil;

/**
 * AES 加密工具类
 *
 * <p>安全说明：
 * <ul>
 *   <li>字符串加解密方法使用 AES/GCM/NoPadding（认证加密，防篡改）</li>
 *   <li>字节数组方法保留 AES/ECB/NoPadding 以兼容现有协议，建议新代码使用 GCM 方法</li>
 * </ul>
 */
public class AESUtil {

    private static final Logger log = LoggerFactory.getLogger(AESUtil.class);

    private static final String KEY_ALGORITHM = "AES";

    /** 推荐模式：AES-GCM（认证加密） */
    private static final String GCM_CIPHER_ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128; // bits
    private static final int GCM_IV_LENGTH = 12;   // bytes

    /**
     * @deprecated ECB 模式不安全，仅保留用于兼容已有协议。新代码请使用 GCM 方法。
     */
    @Deprecated
    private static final String C_DEFAULT_CIPHER_ALGORITHM = "AES/ECB/NoPadding";

    // ====================== GCM 模式（推荐） ======================

    /**
     * AES-GCM 加密，返回 Base64 编码的密文（含 IV 前缀）
     *
     * @param content 待加密内容
     * @param key     加密密钥（字符串形式，将通过 KeyGenerator 派生）
     * @return Base64(IV + ciphertext + tag)，失败返回 null
     */
    public static String encryptString(String content, String key) {
        try {
            byte[] iv = generateIV();
            Cipher cipher = Cipher.getInstance(GCM_CIPHER_ALGORITHM);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, deriveKeyFromString(key), gcmSpec);
            byte[] ciphertext = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
            // 拼接 IV || ciphertext+tag
            byte[] combined = new byte[iv.length + ciphertext.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(ciphertext, 0, combined, iv.length, ciphertext.length);
            return Base64Util.encodeToString(combined);
        } catch (Exception ex) {
            log.error("AES-GCM encrypt failed", ex);
        }
        return null;
    }

    /**
     * AES-GCM 解密，接受 Base64 编码的密文（含 IV 前缀）
     *
     * @param content Base64(IV + ciphertext + tag)
     * @param key     解密密钥（字符串形式）
     * @return 明文，失败返回 null
     */
    public static String decryptBase64Str(String content, String key) {
        try {
            byte[] combined = Base64Util.transformBase64(content);
            byte[] iv = Arrays.copyOfRange(combined, 0, GCM_IV_LENGTH);
            byte[] ciphertext = Arrays.copyOfRange(combined, GCM_IV_LENGTH, combined.length);
            Cipher cipher = Cipher.getInstance(GCM_CIPHER_ALGORITHM);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, deriveKeyFromString(key), gcmSpec);
            byte[] result = cipher.doFinal(ciphertext);
            return new String(result, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            log.error("AES-GCM decrypt failed", ex);
        }
        return null;
    }

    /**
     * AES-GCM 字节加密
     *
     * @param content 明文字节
     * @param key     密钥字节（16/24/32 bytes）
     * @return IV + ciphertext+tag，失败返回 null
     */
    public static byte[] encryptBytesGCM(byte[] content, byte[] key) {
        try {
            byte[] iv = generateIV();
            Cipher cipher = Cipher.getInstance(GCM_CIPHER_ALGORITHM);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, KEY_ALGORITHM), gcmSpec);
            byte[] ciphertext = cipher.doFinal(content);
            byte[] combined = new byte[iv.length + ciphertext.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(ciphertext, 0, combined, iv.length, ciphertext.length);
            return combined;
        } catch (Exception ex) {
            log.error("AES-GCM encryptBytes failed", ex);
        }
        return null;
    }

    /**
     * AES-GCM 字节解密
     *
     * @param content IV + ciphertext+tag
     * @param key     密钥字节
     * @return 明文字节，失败返回 null
     */
    public static byte[] decryptBytesGCM(byte[] content, byte[] key) {
        try {
            byte[] iv = Arrays.copyOfRange(content, 0, GCM_IV_LENGTH);
            byte[] ciphertext = Arrays.copyOfRange(content, GCM_IV_LENGTH, content.length);
            Cipher cipher = Cipher.getInstance(GCM_CIPHER_ALGORITHM);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, KEY_ALGORITHM), gcmSpec);
            return cipher.doFinal(ciphertext);
        } catch (Exception ex) {
            log.error("AES-GCM decryptBytes failed", ex);
        }
        return null;
    }

    // ====================== ECB 模式（兼容旧协议） ======================

    /**
     * AES/ECB/NoPadding 加密 — 仅用于兼容已有二进制协议，新代码请使用 {@link #encryptBytesGCM}
     *
     * @deprecated ECB 模式不安全
     */
    @Deprecated
    public static byte[] encryptBytes(byte[] content, byte[] key) {
        try {
            Cipher cipher = Cipher.getInstance(C_DEFAULT_CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, KEY_ALGORITHM));
            return cipher.doFinal(content);
        } catch (Exception ex) {
            log.error("AES-ECB encrypt failed", ex);
        }
        return null;
    }

    /**
     * @deprecated ECB 模式不安全，请使用 GCM 对应方法
     */
    @Deprecated
    public static String decryptBytes(byte[] content, byte[] key) {
        try {
            Cipher cipher = Cipher.getInstance(C_DEFAULT_CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, KEY_ALGORITHM));
            byte[] result = cipher.doFinal(content);
            return new String(result, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            log.error("AES-ECB decrypt failed", ex);
        }
        return null;
    }

    /**
     * @deprecated ECB 模式不安全，请使用 {@link #decryptBytesGCM}
     */
    @Deprecated
    public static byte[] decryptBytes2bytes(byte[] content, byte[] key) {
        try {
            Cipher cipher = Cipher.getInstance(C_DEFAULT_CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, KEY_ALGORITHM));
            return cipher.doFinal(content);
        } catch (Exception ex) {
            log.error("AES-ECB decrypt failed", ex);
        }
        return null;
    }

    // ====================== 内部方法 ======================

    private static byte[] generateIV() {
        byte[] iv = new byte[GCM_IV_LENGTH];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    private static SecretKeySpec deriveKeyFromString(final String key) throws NoSuchAlgorithmException {
        KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM);
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(key.getBytes(StandardCharsets.UTF_8));
        kg.init(128, random);
        SecretKey secretKey = kg.generateKey();
        return new SecretKeySpec(secretKey.getEncoded(), KEY_ALGORITHM);
    }

    public static void main(String[] args) {
        // GCM 模式演示
        String content = "hello world 你好世界";
        String key = "mySecretKey12345";
        String encrypted = encryptString(content, key);
        System.out.println("GCM encrypted: " + encrypted);
        String decrypted = decryptBase64Str(encrypted, key);
        System.out.println("GCM decrypted: " + decrypted);

        // ECB 兼容演示（保留用于旧协议）
        byte[] keyByte = new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01};
        String hexContent = "24486E5687625ABDBF17D9A2C4171A0194ED8F1E11B3D7090CB6E9106F22EE13";
        byte[] ses = AESUtil.encryptBytes(ByteBufUtil.decodeHexDump(hexContent), keyByte);
        System.out.println("ECB encrypted hex: " + ByteBufUtil.hexDump(ses));

        byte[] encrypedBytes = Base64Util.decode(hexContent.getBytes());
        if (encrypedBytes.length != 32) {
            System.out.println("数据包非 32 字节");
            return;
        }
        byte[] head = new byte[16];
        System.arraycopy(encrypedBytes, 0, head, 0, 16);
        byte[] feet = new byte[16];
        System.arraycopy(encrypedBytes, 16, feet, 0, 16);
        System.out.println("head:" + AESUtil.decryptBytes(head, keyByte));
        System.out.println("feet:" + AESUtil.decryptBytes(feet, keyByte));
    }
}