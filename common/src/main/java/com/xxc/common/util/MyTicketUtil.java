package com.xxc.common.util;

import cn.hutool.core.util.StrUtil;
import com.xxc.common.consts.RedisKey;
import io.netty.handler.codec.http.FullHttpRequest;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @author xixincan
 * 2020-03-10
 * @version 1.0.0
 */
public class MyTicketUtil {

    public static String getTicket(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (null == cookies) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (RedisKey.TICKET.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public static String getTicket(FullHttpRequest fullHttpRequest) {
        String cookieStr = fullHttpRequest.headers().get("Cookie");
        if (StrUtil.isEmpty(cookieStr)) {
            return null;
        }
        String[] cookieArr = cookieStr.split(";");
        for (String cookie : cookieArr) {
            if (StrUtil.isNotEmpty(cookie) && cookie.contains(RedisKey.TICKET)) {
                return cookie.replace(RedisKey.TICKET + "=", "");
            }
        }
        return null;
    }

    public static String genTicket(String uid) {
        if (null != uid) {
            return EncryptUtil.encodeBase64(uid);
        }
        return null;
    }

    public static String getUid(String ticket) {
        if (StrUtil.isNotEmpty(ticket)) {
            return EncryptUtil.decodeBase64ToString(ticket);
        }
        return null;
    }

}
