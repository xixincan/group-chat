package com.xxc.common.util;

import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xixincan
 * 2020-03-10
 * @version 1.0.0
 */
public class CookieUtil {

    public static String getValue(HttpServletRequest request, String key) {
        if (StringUtils.isEmpty(key)) {
            throw new NullPointerException("Cookie key can not be null");
        }

        Cookie cookies[] = request.getCookies();
        Cookie cookie = null;
        for (int i = 0; cookies != null && i < cookies.length; i++) {
            if (cookies[i] != null && key.equals(cookies[i].getName())) {
                cookie = cookies[i];
                break;
            }
        }
        if (cookie != null) {
            return cookie.getValue();
        }

        return null;
    }

    private static String encode(String value) {
        if (StringUtils.isEmpty(value))
            return value;
        value = EncryptUtil.encodeBase64(Des.encrypt(value));
        return value;
    }

    public static String decode(String value) {
        if (StringUtils.isEmpty(value))
            return value;
        value = Des.decrypt(EncryptUtil.decodeBase64ToString(value));
        return value;
    }

    public static void addCookie(HttpServletRequest request, HttpServletResponse response, String cookieName, String cookieValue) {
        addCookie(request, response, cookieName, cookieValue, -1);
    }

    public static void addCookie(String domain, HttpServletRequest request, HttpServletResponse response, String cookieName, String cookieValue) {
        addCookie(domain, request, response, cookieName, cookieValue, -1);
    }

    public static void addEncryptCookie(HttpServletRequest request, HttpServletResponse response, String cookieName, String cookieValue, int expiry) {
        if (null != cookieValue) {
            cookieValue = CookieUtil.encode(cookieValue);
        }
        addCookie(request, response, cookieName, cookieValue, expiry);
    }

    public static void addEncryptCookie(HttpServletRequest request, HttpServletResponse response, String cookieName, String cookieValue) {
        if (null != cookieValue) {
            cookieValue = CookieUtil.encode(cookieValue);
        }
        addCookie(request, response, cookieName, cookieValue, -1);
    }

    public static Cookie addCookie(HttpServletRequest request, HttpServletResponse response, String cookieName, String cookieValue, int expiry) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setPath("/");
        //如果request.getServerName()为localhost或者IP地址都可以不用设置domain
        String domain = resolveTopDomain(request.getServerName());
        if (!StringUtils.isEmpty(domain)) {
            cookie.setDomain("." + domain);
        }
        cookie.setMaxAge(expiry);
        response.addCookie(cookie);
        return cookie;
    }

    public static Cookie addCookie(String domain, HttpServletRequest request, HttpServletResponse response, String cookieName, String cookieValue, int expiry) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setPath("/");
        domain = resolveTopDomain(domain);
        if (!StringUtils.isEmpty(domain)) {
            cookie.setDomain("." + domain);
        }
        cookie.setMaxAge(expiry);
        response.addCookie(cookie);
        return cookie;
    }

    public static void removeCookie(HttpServletRequest request, HttpServletResponse response, String cookieName) {
        addCookie(request, response, cookieName, null, 0);
    }

    public static void removeCookie(String domain, HttpServletRequest request, HttpServletResponse response, String cookieName) {
        addCookie(domain, request, response, cookieName, null, 0);
    }

    public static String resolveTopDomain(String domain) {
        if(StringUtils.isEmpty(domain)){
            return null;
        }

        //如果request.getServerName()为localhost或者IP地址返回null
        if ((!NetUtil.isValidAddress(domain)) && !NetUtil.isLocalHost(domain)) {
			/*String[] ds = domain.split("\\.");
			int length = ds.length;
			domain = ds[length - 2] + StringCsts.POINT_CHARACTER + ds[length - 1];
			return domain;*/
            //Pattern p = Pattern.compile("[^.]*(\\.(com|cn|net|org|biz|info|cc|tv))+",Pattern.CASE_INSENSITIVE);
            Pattern p = Pattern.compile("[^(.|http://)]*(\\.(com|cn|net|org|biz|info|cc|tv))+",Pattern.CASE_INSENSITIVE);
            Matcher matcher = p.matcher(domain);
            if(matcher.find()){
                return matcher.group();
            }
            return null;
        }
        return null;
    }

    public static Map<String, Cookie> toMap(Cookie[] cookies) {
        if (cookies == null || cookies.length == 0)
            return Collections.emptyMap();
        Map<String, Cookie> map = new HashMap<String, Cookie>(cookies.length);
        for (Cookie c : cookies) {
            map.put(c.getName(), c);
        }
        return map;
    }

}
