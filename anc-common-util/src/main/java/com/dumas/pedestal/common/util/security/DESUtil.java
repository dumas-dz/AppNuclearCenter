package com.dumas.pedestal.common.util.security;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBufUtil;

/**
 * DES 加密工具类
 *
 * <p><b>安全警告：</b>DES 算法已被认为不安全（56位密钥可被暴力破解）。
 * 建议新代码使用 {@link AESUtil} 的 GCM 方法。
 * 本类仅保留用于兼容已有系统。
 *
 * @author andaren
 * @version V1.0
 * @since 2020-05-03 20:17
 * @deprecated 使用 {@link AESUtil} 替代
 */
@Deprecated
public class DESUtil {
    private static final Logger log = LoggerFactory.getLogger(DESUtil.class);

    private static final String DES = "DES";
    private static final String C_DES = "DES/ECB/NoPadding";

    public static void main(String args[]) {
        String sourceHex = "12345678123456781234567812345678";
        byte[] password = "12345678".getBytes();

        byte[] result = DESUtil.encrypt(sourceHex.getBytes(), password);
        System.out.println("加密后：" + new String(result));
        System.out.println(ByteBufUtil.hexDump(result));
        try {
            byte[] decryResult = DESUtil.decrypt(result, password);
            System.out.println("解密后：" + new String(decryResult));
        } catch (Exception e1) {
            log.error("decrypt failed", e1);
        }
    }

    /**
     * 加密
     *
     * @deprecated DES 不安全，请使用 {@link AESUtil#encryptBytesGCM(byte[], byte[])}
     */
    @Deprecated
    public static byte[] encrypt(byte[] datasource, byte[] password) {
        try {
            SecureRandom random = new SecureRandom();
            DESKeySpec desKey = new DESKeySpec(password);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
            SecretKey securekey = keyFactory.generateSecret(desKey);
            Cipher cipher = Cipher.getInstance(C_DES);
            cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
            return cipher.doFinal(datasource);
        } catch (Exception e) {
            log.error("DES encrypt failed", e);
        }
        return null;
    }

    /**
     * 解密
     *
     * @deprecated DES 不安全，请使用 {@link AESUtil#decryptBytesGCM(byte[], byte[])}
     */
    @Deprecated
    public static byte[] decrypt(byte[] src, byte[] password) throws Exception {
        SecureRandom random = new SecureRandom();
        DESKeySpec desKey = new DESKeySpec(password);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(desKey);
        Cipher cipher = Cipher.getInstance(C_DES);
        cipher.init(Cipher.DECRYPT_MODE, securekey, random);
        return cipher.doFinal(src);
    }
}
