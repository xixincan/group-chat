package com.xxc.web.controller;

import com.xxc.entity.response.UserInfo;
import com.xxc.entity.result.MyResult;
import com.xxc.service.IUserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @Author: xixincan
 * @Date: 2019/5/20
 * @Version 1.0
 */
@RestController
@RequestMapping("user")
public class UserController {

    @Resource
    private IUserService userService;

    @PostMapping("fetch/info")
    @ResponseBody
    public MyResult<UserInfo> getUserInfo(HttpServletRequest request) {
        return MyResult.success(this.userService.getUserInfo(request));
    }

}
