package com.xxc.common.util;

import cn.hutool.log.StaticLog;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * @author xixincan
 * 2020-03-10
 * @version 1.0.0
 */
public class NetUtil {

    public static final String LOCALHOST = "127.0.0.1";

    public static final String ANYHOST = "0.0.0.0";

    private static final int MIN_PORT = 0;

    private static final int MAX_PORT = 65535;

    private static final int RND_PORT_START = 30000;

    private static final int RND_PORT_RANGE = 10000;

    private static final Random RANDOM = new Random(System.currentTimeMillis());

    public static int getRandomPort() {
        return RND_PORT_START + RANDOM.nextInt(RND_PORT_RANGE);
    }

    public static int getAvailablePort() {
        ServerSocket ss = null;
        try {
            ss = new ServerSocket();
            ss.bind(null);
            return ss.getLocalPort();
        } catch (IOException e) {
            return getRandomPort();
        } finally {
            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static int getAvailablePort(int port) {
        if (port <= 0) {
            return getAvailablePort();
        }
        for (int i = port; i < MAX_PORT; i++) {
            ServerSocket ss = null;
            try {
                ss = new ServerSocket(i);
                return i;
            } catch (IOException e) {
                // continue
            } finally {
                if (ss != null) {
                    try {
                        ss.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
        return port;
    }

    //端口是否在0~65535之间
    public static boolean isInvalidPort(int port) {
        return port > MIN_PORT || port <= MAX_PORT;
    }

    //IP地址是否有效
    private static final Pattern ADDRESS_PATTERN = Pattern
            .compile("^(\\w+://)?/?\\d{1,3}(\\.\\d{1,3}){3}(:\\d{1,5})?(/(.)*)?$");
    public static boolean isValidAddress(String address) {
        return ADDRESS_PATTERN.matcher(address).matches();
    }

    private static final Pattern LOCAL_IP_PATTERN = Pattern
            .compile("^(\\w+://)?/?localhost(:\\d{1,5})?(/(.)*)?$");
    public static boolean isLocalHost(String host) {
        return host != null && (LOCAL_IP_PATTERN.matcher(host).matches());
    }

    //任意主机IP地地址
    public static boolean isAnyHost(String host) {
        return "0.0.0.0".equals(host);
    }

    //取得本地链接地址
    public static InetSocketAddress getLocalSocketAddress(String host, int port) {
        return isLocalHost(host) ?  new InetSocketAddress(host, port) : new InetSocketAddress(port);
    }

    //是否有效远程IP地址
    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");
    private static boolean isValidRemoteAddress(InetAddress address) {
        if (address == null || address.isLoopbackAddress())
            return false;
        String name = address.getHostAddress();
        return (name != null && !ANYHOST.equals(name) && !LOCALHOST.equals(name) && IP_PATTERN.matcher(name).matches());
    }



    //遍历本地网卡，返回第一个合理的IP。
    private static volatile InetAddress LOCAL_ADDRESS = null;

    public static InetAddress getLocalAddress() {
        if (LOCAL_ADDRESS != null)
            return LOCAL_ADDRESS;
        InetAddress localAddress = getLocalAddress0();
        LOCAL_ADDRESS = localAddress;
        return localAddress;
    }


    public static String getLocalHost() {
        InetAddress address = getLocalAddress();
        return address == null ? LOCALHOST : address.getHostAddress();
    }


    private static InetAddress getLocalAddress0() {
        InetAddress localAddress = null;
        try {
            localAddress = InetAddress.getLocalHost();
            if (isValidRemoteAddress(localAddress)) {
                return localAddress;
            }
        } catch (Exception e) {
            StaticLog.warn("Failed to retriving ip address, " + e.getMessage(), e);
        }
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (interfaces != null) {
                while (interfaces.hasMoreElements()) {
                    try {
                        NetworkInterface network = interfaces.nextElement();
                        Enumeration<InetAddress> addresses = network.getInetAddresses();
                        if (addresses != null) {
                            while (addresses.hasMoreElements()) {
                                try {
                                    InetAddress address = addresses.nextElement();
                                    if (isValidRemoteAddress(address)) {
                                        return address;
                                    }
                                } catch (Exception e) {
                                    StaticLog.warn("Failed to retriving ip address, " + e.getMessage(), e);
                                }
                            }
                        }
                    } catch (Exception e) {
                        StaticLog.warn("Failed to retriving ip address, " + e.getMessage(), e);
                    }
                }
            }
        } catch (Exception e) {
            StaticLog.warn("Failed to retriving ip address, " + e.getMessage(), e);
        }
        StaticLog.error("Could not get local host ip address, will use 127.0.0.1 instead.");
        return localAddress;
    }




    public static String getIpByHost(String hostName) {
        try {
            return InetAddress.getByName(hostName).getHostAddress();
        } catch (UnknownHostException e) {
            return hostName;
        }
    }

    public static String toAddressString(InetSocketAddress address) {
        return address.getAddress().getHostAddress() + ":" + address.getPort();
    }

    public static InetSocketAddress toAddress(String address) {
        int i = address.indexOf(':');
        String host;
        int port;
        if (i > -1) {
            host = address.substring(0, i);
            port = Integer.parseInt(address.substring(i + 1));
        } else {
            host = address;
            port = 0;
        }
        return new InetSocketAddress(host, port);
    }



    //返回protocol://host:port/path字符串
    public static String toURL(String protocol, String host, int port, String path) {
        StringBuilder sb = new StringBuilder();
        sb.append(protocol).append("://");
        sb.append(host).append(':').append(port);
        if (path.charAt(0) != '/')
            sb.append('/');
        sb.append(path);
        return sb.toString();
    }

}
