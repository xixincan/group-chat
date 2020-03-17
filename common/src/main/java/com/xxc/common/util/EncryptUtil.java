package com.xxc.common.util;

import cn.hutool.log.StaticLog;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * @author xixincan
 * 2020-03-10
 * @version 1.0.0
 */
public class EncryptUtil {

    private static final String DEFAULT_ENCODING = "UTF-8";

    static char[] chars = new char[]{'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static String md5(String input) {
        try {
            //获取MD5实例
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            // 处理成十六进制的字符串(通常)
            for (byte bb : digest) {
                sb.append(chars[(bb >> 4) & 15]);
                sb.append(chars[bb & 15]);
            }
            // 打印加密后的字符串
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            StaticLog.error(e);
            return input;
        }
    }
    public static String genRandomID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    /**
     * Base64编码.
     */
    public static String encodeBase64(byte[] input) {
        return Base64.encode(input);
    }

    public static String encodeBase64(String encrypt) {
        return encodeBase64(encrypt.getBytes());
    }

    /**
     * Base64解码.
     */
    public static byte[] decodeBase64(String input) {
        return Base64.decode(input);
    }

    public static String decodeBase64ToString(String value) {
        try {
            return new String(decodeBase64(value), DEFAULT_ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String encrypt(String text) {
        return Des.encrypt(text);
    }

    public static String decrypt(String cipher) {
        return Des.decrypt(cipher);
    }

    /**
     * URL 编码, Encode默认为UTF-8.
     */
    public static String urlEncode(String part) {
        try {
            return URLEncoder.encode(part, DEFAULT_ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * URL 解码, Encode默认为UTF-8.
     */
    public static String urlDecode(String part) {

        try {
            return URLDecoder.decode(part, DEFAULT_ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 转义HTML
     *
     * @param html
     * @return
     */
    public static String htmlEscape(String html) {
        if (html == null || html.trim().length() < 1)
            return "";
        html = html.replaceAll("<", "&lt;");
        html = html.replaceAll(">", "&gt;");
        html = html.replaceAll("'", "&apos;");
        html = html.replaceAll("\"", "&quot;");
        html = html.replaceAll(" ", "&nbsp;");
        return html;
    }

    public static void main(String[] args) {
        String s = EncryptUtil.encodeBase64("112233");
        System.out.println(s);
        String string = EncryptUtil.decodeBase64ToString(s);
        System.out.println(string);
    }

}
