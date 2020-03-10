package com.xxc.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.log.StaticLog;
import com.xxc.dao.mapper.IpBlackListMapper;
import com.xxc.dao.mapper.IpWhiteListMapper;
import com.xxc.dao.model.IpBlackList;
import com.xxc.dao.model.IpWhiteList;
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
    private IpWhiteListMapper whiteListMapper;

    @Resource
    private IpBlackListMapper blackListMapper;

    @Override
    public void reload() {
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
}
