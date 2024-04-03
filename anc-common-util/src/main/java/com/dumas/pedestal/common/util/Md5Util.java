package com.dumas.pedestal.common.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import cn.hutool.core.exceptions.UtilException;
import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * TODO
 *
 * @author andaren
 * @version V1.0
 * @since 2019-12-27 09:33
 */
@Slf4j
public class Md5Util {
    private static final String MD5 = "MD5";

    /**
     * 将实体进行json序列化后md5
     * @param obj
     * @return
     */
    public static String md5Obj(Object obj) {
        return DigestUtils.md5Hex(JSONObject.toJSONString(obj)).toUpperCase();
    }

    /**
     * 将 字符串进行MD5后转化成16进制字符串
     * @param s
     * @return
     */
    public static String md5And2Hex(String s) {
        try {
            return new String(toHex(md5(s)).getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("not supported charset[" + s + "]", e);
            throw new UtilException(e);
        }
    }

    /**
     * 将源字符串 MD5 后 转大写
     * @param src
     * @return
     */
    public static String md5And2Uppercase(String src) {
        try {
            return new String(md5And2Hex(src)).toUpperCase();
        } catch (Exception e) {
            log.error("not supported charset[" + src + "]", e);
            throw new UtilException(e);
        }
    }

    private static byte[] md5(String s) {
        MessageDigest algorithm;
        try {
            algorithm = MessageDigest.getInstance(MD5);
            algorithm.reset();
            algorithm.update(s.getBytes(StandardCharsets.UTF_8));
            byte[] messageDigest = algorithm.digest();
            return messageDigest;
        } catch (Exception e) {
            log.error("MD5 Error...", e);
        }
        return null;
    }

    private static final String toHex(byte hash[]) {
        if (hash == null) {
            return null;
        }
        StringBuffer buf = new StringBuffer(hash.length * 2);
        int i;

        for (i = 0; i < hash.length; i++) {
            if ((hash[i] & 0xff) < 0x10) {
                buf.append("0");
            }
            buf.append(Long.toString(hash[i] & 0xff, 16));
        }
        return buf.toString();
    }

    public static void main(String[] args) {

        String testS = "timestamp=2020-03-30 11:59:00&schoolId=600585";
        System.out.println(md5And2Uppercase(testS));
    }
}
