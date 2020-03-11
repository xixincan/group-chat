package com.xxc.service;

import java.util.List;

/**
 * @author xixincan
 * 2020-03-10
 * @version 1.0.0
 */
public interface IIpPlanService {

    void reload();

    List<String> getWhiteList();

    List<String> getBlackList();

    boolean isWhite(String ipAddr);

    boolean isBlack(String ipAddr);

    boolean checkIpAddr(String ipAddr);
}
