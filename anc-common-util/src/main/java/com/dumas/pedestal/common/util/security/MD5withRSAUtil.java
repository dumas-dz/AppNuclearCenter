package com.dumas.pedestal.common.util.security;

import cn.hutool.core.exceptions.UtilException;
import com.dumas.pedestal.common.util.Base64Util;
import com.dumas.pedestal.common.util.ByteUtil;

import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;

/**
 * 参考地址
 *  https://zhuanlan.zhihu.com/p/25150187
 *
 * @author andaren
 * @version V1.0
 * @since 2020-03-10 16:39
 */
@Slf4j
public final class MD5withRSAUtil {
    private static final String RSA = "RSA";
    private static final String SIGNATURE_ALGORITHM = "MD5withRSA";
    private MD5withRSAUtil() {
        throw new AssertionError();
    }

    /**
     * 基于java的Signature API 的私钥签名
     * @param rsaPrivateKey rsa私钥
     * @param src           加密内容
     * @return 签名
     */
    public static byte[] sign(String rsaPrivateKey, String src) {
        Objects.requireNonNull(rsaPrivateKey, "rsa私钥");
        Objects.requireNonNull(src, "源数据");

        try {
            // 不一定所有私钥都会用base64编码过
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64Util.transformBase64(rsaPrivateKey));
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            //初始化私钥
            signature.initSign(privateKey);
            //传入签名内容
            signature.update(src.getBytes());
            //生成签名
            byte[] result = signature.sign();
            return result;
        } catch (NoSuchAlgorithmException e1) {
            log.error("签名算法不存在 [" + src + "]", e1);
            throw new UtilException(e1);
        } catch (InvalidKeySpecException e2) {
            log.error("签名异常: 指定了非法的私钥 [" + src + "]", e2);
            throw new UtilException(e2);
        } catch (InvalidKeyException e3) {
            log.error("签名异常: 私钥非法 [" + src + "]", e3);
            throw new UtilException(e3);
        } catch (SignatureException e4) {
            log.error("签名异常 [" + src + "]", e4);
            throw new UtilException(e4);
        }
    }

    /**
     * 检验签名
     * @param rsaPublicKey  rsa公钥
     * @param src           加密源数据
     * @param sign          签名【注意：有时候签名不是一定是16进制的字符串】
     * @return  签名校验结果[true: 验证通过]
     */
    public static boolean validateSign(String rsaPublicKey, String src, String sign) {
        Objects.requireNonNull(rsaPublicKey, "rsa公钥");
        Objects.requireNonNull(src, "加密源数据");
        Objects.requireNonNull(sign, "待验证的签名");

        try {
            // 不一定所有公钥都会用base64编码过
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(Base64Util.transformBase64(rsaPublicKey));
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            // 初始化公钥
            signature.initVerify(publicKey);
            // 传入签名内容
            signature.update(src.getBytes(Charset.forName("UTF-8")));
            // 核对签名
            boolean validateResult = signature.verify(ByteUtil.hexStringToBytes(sign));
            return validateResult;
        } catch (NoSuchAlgorithmException e1) {
            log.error("签名算法不存在 [" + src + "]", e1);
            throw new UtilException(e1);
        } catch (InvalidKeySpecException e2) {
            log.error("签名验证异常: 指定了非法的私钥 [" + src + "]", e2);
            throw new UtilException(e2);
        } catch (InvalidKeyException e3) {
            log.error("签名验证异常: 私钥非法 [" + src + "]", e3);
            throw new UtilException(e3);
        } catch (SignatureException e4) {
            log.error("签名验证异常 [" + src + "]", e4);
            throw new UtilException(e4);
        }
    }

}
