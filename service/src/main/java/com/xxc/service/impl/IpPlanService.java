package com.xxc.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.log.StaticLog;
import com.xxc.common.consts.ConfigKey;
import com.xxc.entity.enums.IpPlanEnum;
import com.xxc.dao.mapper.IpBlackListMapper;
import com.xxc.dao.mapper.IpWhiteListMapper;
import com.xxc.dao.model.IpBlackList;
import com.xxc.dao.model.IpWhiteList;
import com.xxc.service.IConfigService;
import com.xxc.service.IIpPlanService;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xixincan
 * 2020-03-10
 * @version 1.0.0
 */
@Service
public class IpPlanService implements IIpPlanService {

    private static List<String> whiteLists = null;

    private static List<String> blackLists = null;

    @Resource
    private IConfigService configService;
    @Resource
    private IpWhiteListMapper whiteListMapper;
    @Resource
    private IpBlackListMapper blackListMapper;

    @Override
    public void reload() {
        //todo 修改为Redis缓存
        Example example = new Example(IpBlackList.class);
        example.createCriteria().andEqualTo("valid", Boolean.TRUE);
        List<IpBlackList> ipBlackLists = this.blackListMapper.selectByExample(example);
        blackLists = ipBlackLists.stream().map(IpBlackList::getIpAddr).collect(Collectors.toList());
        StaticLog.info("IP黑名单:{}", blackLists);

        example = new Example(IpWhiteList.class);
        example.createCriteria().andEqualTo("valid", Boolean.TRUE);
        List<IpWhiteList> ipWhiteLists = this.whiteListMapper.selectByExample(example);
        whiteLists = ipWhiteLists.stream().map(IpWhiteList::getIpAddr).collect(Collectors.toList());
        StaticLog.info("IP白名单:{}", whiteLists);

    }

    @Override
    public List<String> getWhiteList() {
        if (CollectionUtil.isEmpty(whiteLists)) {
            this.reload();
        }
        return whiteLists;
    }

    @Override
    public List<String> getBlackList() {
        if (CollectionUtil.isEmpty(blackLists)) {
            this.reload();
        }
        return blackLists;
    }

    @Override
    public boolean isWhite(String ipAddr) {
        return this.getWhiteList().contains(ipAddr);
    }

    @Override
    public boolean isBlack(String ipAddr) {
        return this.getBlackList().contains(ipAddr);
    }

    @Override
    public boolean checkIpAddr(String ipAddr) {
        Integer ip_plan = this.configService.getIntegerValue(ConfigKey.IP_PLAN);
        IpPlanEnum planEnum = IpPlanEnum.find(ip_plan);
        switch (planEnum) {
            case WHITE_ACCESS:
                if (this.isWhite(ipAddr)) {
                    break;
                }
                StaticLog.warn("非白名单IP访问阻止:IP={}", ipAddr);
                return false;
            case BLACK_DENIED:
                if (this.isBlack(ipAddr)) {
                    StaticLog.warn("黑名单IP访问阻止:IP={}", ipAddr);
                    return false;
                }
                break;
            default:
                break;
        }
        return true;
    }
}
