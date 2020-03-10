package com.xxc.common.util;

import cn.hutool.log.StaticLog;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @version 1.0.0
 * @Author: xixincan
 * 2019-08-22
 */
public class MyIPUtil {

    private MyIPUtil() {
    }

    /**
     * 如果使用了反向代理软件, 经过代理以后，由于在客户端和服务之间增加了中间层，request.getRemoteAddr（）的方法获取的IP实际上是代理服务器的地址,
     * 因此服务器无法直接拿到客户端的IP;
     * 但是在转发请求的HTTP头信息中，增加了X－FORWARDED－FOR信息。用以跟踪 原有的客户端IP地址和原来客户端请求的服务器地址。
     * <p>
     * 可是，如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP值，究竟哪个才是真正的用户端的真实IP呢？
     * <p>
     * 答案是取 X-Forwarded-For中第一个非unknown的有效IP字符串。
     * 如： X-Forwarded-For：192.168.1.110， 192.168.1.120， 192.168.1.130， 192.168.1.100
     * 用户真实IP为： 192.168.1.110
     *
     * @param request http request
     * @return remote ip addr
     */
    public static String getRemoteIpAddr(HttpServletRequest request) {

        String ip = request.getHeader("x-forwarded-for");

        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }

    public static boolean isLocalHost(String ipAddr) {
        return (!StringUtils.isEmpty(ipAddr)) && (ipAddr.contains("127.0.0.1") || ipAddr.contains("localhost"));
    }

    public static String getIP(String remoteAddr) {
        StaticLog.info(remoteAddr);
        return remoteAddr.substring(1, remoteAddr.indexOf(":"));
    }
}
