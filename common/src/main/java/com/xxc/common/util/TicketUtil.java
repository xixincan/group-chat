package com.xxc.common.util;

import cn.hutool.core.util.StrUtil;
import com.xxc.common.consts.ConfigKey;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @author xixincan
 * 2020-03-10
 * @version 1.0.0
 */
public class TicketUtil {

    public static String getTicket(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (ConfigKey.TICKET.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public static String genTicket(String uid) {
        return null;
    }

    public static String getUid(String ticket) {
        if (StrUtil.isNotEmpty(ticket)) {
            return EncryptUtil.decodeBase64ToString(ticket);
        }
        return null;
    }

}
