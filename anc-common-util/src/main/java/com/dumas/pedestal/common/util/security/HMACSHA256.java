package com.dumas.pedestal.common.util.security;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.alibaba.fastjson2.annotation.JSONField;
import org.apache.http.util.Asserts;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * HMAC是密钥相关的哈希运算消息认证码（Hash-based Message Authentication Code）的缩写，
 * 由H.Krawezyk，M.Bellare，R.Canetti于1996年提出的一种基于Hash函数和密钥进行消息认证的方法，
 * 并于1997年作为RFC2104被公布，并在IPSec和其他网络协议（如SSL）中得以广泛应用，
 * 现在已经成为事实上的Internet安全标准。它可以与任何迭代散列函数捆绑使用。
 *
 * @author andaren
 * @version V1.0
 * @since 2020-03-27 15:19
 */
@Slf4j
public class HMACSHA256 {
    private static final String SHA256_HMAC = "HmacSHA256";

    /**
     * 将加密后的字节数组转换成字符串
     *
     * @param b 字节数组
     * @return 字符串
     */
    public  static String byteArrayToHexString(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (int n = 0; b!=null && n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1) {
                hs.append('0');
            }
            hs.append(stmp);
        }
        return hs.toString().toLowerCase();
    }
    /**
     * sha256_HMAC加密
     * @param message 消息
     * @param secret  秘钥
     * @return 加密后字符串
     */
    public static String sha256_HMAC(String message, String secret) {
        String hash = "";
        try {
            Mac sha256_HMAC = Mac.getInstance(SHA256_HMAC);
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), SHA256_HMAC);
            sha256_HMAC.init(secret_key);
            byte[] bytes = sha256_HMAC.doFinal(message.getBytes());
            hash = byteArrayToHexString(bytes);
        } catch (Exception e) {
            log.error("Error HmacSHA256 : {}", e.getMessage());
            return "";
        }
        return hash;
    }

    public static void main(String[] args) {
        RedirectReqParam param = new RedirectReqParam();
        param.setCropId("ding76e1d57db287753835c2f4657eb6378f");
        param.setTimeStamp("1585222291148");
        param.setUserId("1");
        param.makeSign();

        /**
         * https://collect.rzliot.com/WCUAuthWebUI.DD/AuthYZGPC/Index?CropId=ding76e1d57db287753835c2f4657eb6378f&UserId=1&TimeStamp=1585222291148&Signature=c9e7768ae86f38a96fcc41461292396ec9f7e857d67c744faedd5d4f588dcf73
         */
        StringBuilder reqUrlSb = new StringBuilder("https://collect.rzliot.com/WCUAuthWebUI.DD/AuthYZGPC/Index?");
        reqUrlSb.append("CropId=").append(param.getCropId())
            .append("&UserId=").append(param.getUserId())
            .append("&TimeStamp=").append(param.getTimeStamp())
            .append("&Signature=").append(param.getSignature());

        System.out.println(reqUrlSb.toString());
    }


}

@Data
@Slf4j
class RedirectReqParam {
    /**
     * 企业第三方Id
     */
    @JSONField(name = "CropId")
    String cropId;
    /**
     * 钉钉第三方Id
     */
    @JSONField(name = "UserId")
    String userId;
    /**
     * 毫秒时间戳
     */
    @JSONField(name = "TimeStamp")
    String timeStamp;
    /**
     * 计算结果
     */
    @JSONField(name = "Signature")
    String signature;

    private String makeSignMessage() {
        StringBuilder signSb = new StringBuilder();
        signSb.append(timeStamp).append(cropId).append(userId);
        return signSb.toString();
    }

    public void makeSign() {
        Asserts.notBlank(cropId, "企业第三方Id");
        Asserts.notBlank(userId, "钉钉第三方Id");
        Asserts.notBlank(timeStamp, "毫秒时间戳");

        String message = makeSignMessage();
        String secret = this.userId;
        this.signature = HMACSHA256.sha256_HMAC(message, secret);
    }

}
