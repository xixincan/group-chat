package com.xxc.common.util;

import com.xxc.entity.exp.AccessException;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xixincan
 * 2020-03-09
 * @version 1.0.0
 */
public class Member {

    private static final Logger LOGGER = LoggerFactory.getLogger(Member.class);

    public static final Map<String, String> WHITE_MAP = new ConcurrentHashMap<>();

    public static final Map<Channel, String> REG_TAB = new ConcurrentHashMap<>();

    static {
       WHITE_MAP.put("192.168.24.61", "shaowenxing");
       WHITE_MAP.put("192.168.24.62", "wanglin");
       WHITE_MAP.put("192.168.24.69", "wangfeng");
       WHITE_MAP.put("192.168.148.99", "xixincan");
       WHITE_MAP.put("192.168.24.185", "xixincan");
       WHITE_MAP.put("127.0.0.1", "xixincan");
       WHITE_MAP.put("localhost", "xixincan");
    }

    public static String getIP(String remoteAddr) {
        LOGGER.info(remoteAddr);
        return remoteAddr.substring(1, remoteAddr.indexOf(":"));
    }

    public static void checkIP(String ipAddr) {
        if (!WHITE_MAP.keySet().contains(ipAddr)) {
            LOGGER.error("不在IP白名单内，拒绝访问{}", ipAddr);
            throw new AccessException("Access denied.");
        }
    }

    public static String getNickname(String ipAddr) {
        return WHITE_MAP.get(ipAddr);
    }
}
