package com.dumas.pedestal.common.util;

import cn.hutool.crypto.SecureUtil;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author Dumas
 * 生成签名sign
 */
public class SignUtil {

    private static Logger log = LoggerFactory.getLogger(SignUtil.class);

    public final static String PARAM_SPLIT = "|";

    /**
     * 蝌蚪签名规则
     *
     * @param signKey
     * @param params
     * @return
     */
    public static String sign(String signKey, TreeMap<String, Object> params) {
        Preconditions.checkState(StringUtils.isNotEmpty(signKey), "未指定签名MD5Key");
        Preconditions.checkState(params != null && !params.isEmpty(), "未指定需要签名的参数");

        TreeMap<String, String> treeMap = new TreeMap<>(new MapKeyComparator());
        for (Map.Entry<String, Object> entry : params.entrySet()
        ) {
            treeMap.put(entry.getKey(), StringUtils.join(entry.getValue(), PARAM_SPLIT));
        }
        List<String> allList = new LinkedList<>();
        for (String value : treeMap.values()) {
            allList.add(value);
        }
        allList.add(signKey);
        String sign = StringUtils.join(allList.iterator(), "");
        log.debug("签名数据<原文>：{}", sign);
        String encodeSign = SecureUtil.md5(sign).toUpperCase();
        log.debug("签名数据<MD5>：{}", encodeSign);
        return encodeSign;
    }

    /**
     * 大数据签名规则
     *
     * @param signKey
     * @param params
     * @return
     */
    public static String signDB(String signKey, TreeMap<String, Object> params) {
        Preconditions.checkState(StringUtils.isNotEmpty(signKey), "未指定签名MD5Key");
        Preconditions.checkState(params != null && !params.isEmpty(), "未指定需要签名的参数");
        log.debug("参数名称: {}", params.keySet());
        String sign = Joiner.on('|').useForNull("").join(params.values()) + "|" + "***";
        log.debug("签名数据<原⽂>: {}", sign);
        String encodeSign = CodecUtil.encodeUrl(sign);
        log.debug("签名数据<URLEncode>: {}", encodeSign);
        encodeSign = CodecUtil.encodeMD5AsHexString(encodeSign);
        log.debug("签名数据<MD5>: {}", encodeSign);
        return encodeSign;
    }

    /**
     * 短信签名
     *
     * @param signKey
     * @param params
     * @return
     */
    public static String signSMS(String signKey, TreeMap<String, Object> params) {
        Preconditions.checkState(StringUtils.isNotEmpty(signKey), "未指定签名MD5Key");
        Preconditions.checkState(params != null && !params.isEmpty(), "未指定需要签名的参数");

        StringBuilder sign = new StringBuilder();
        for (Map.Entry<String, Object> entry : params.entrySet()
        ) {
            if (Objects.isNull(entry.getValue()) || entry.getKey().equalsIgnoreCase("sign") || entry.getKey().equalsIgnoreCase("signType")) {
                continue;
            }
            sign.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        sign.append(signKey);
        String signStr = SecureUtil.md5(sign.toString());
        return signStr;
    }

    static class MapKeyComparator implements Comparator<String> {

        @Override
        public int compare(String o1, String o2) {
            return o1.compareTo(o2);
        }
    }

}
