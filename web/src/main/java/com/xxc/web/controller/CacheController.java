package com.xxc.web.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import com.xxc.common.util.MyIPUtil;
import com.xxc.entity.annotation.SkipLoginCheck;
import com.xxc.entity.exp.AccessException;
import com.xxc.entity.result.MyResult;
import com.xxc.service.IConfigService;
import com.xxc.service.IIpPlanService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author xixincan
 * 2020-03-10
 * @version 1.0.0
 */
@RestController
@RequestMapping("cache")
public class CacheController {

    @Resource
    private IConfigService configService;

    @Resource
    private IIpPlanService ipPlanService;

    @SkipLoginCheck
    @RequestMapping(value = "reload", method = {RequestMethod.GET, RequestMethod.POST})
    public String reload(HttpServletRequest request, String key) {
        if (MyIPUtil.isLocalHost(MyIPUtil.getRemoteIpAddr(request))) {
            if (StrUtil.isEmpty(key)) {
                this.configService.reload();
                this.ipPlanService.reload();
            } else if ("config".equals(key)) {
                this.configService.reload();
            } else if ("ipPlan".equals(key)) {
                this.ipPlanService.reload();
            } else {
                return "Unknown KEY";
            }
            return "SUCCESSFUL";
        }
        throw new AccessException("Access Denied.");
    }

    @SkipLoginCheck
    @RequestMapping(value = "get", method = {RequestMethod.GET, RequestMethod.POST})
    public MyResult<String> get(String key) {
        return MyResult.success(this.configService.getValue(key));
    }

}
