package com.xxc.web.controller;

import com.xxc.service.IUserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author: xixincan
 * @Date: 2019/5/20
 * @Version 1.0
 */
@RestController
@RequestMapping("user")
public class UserController {

    @Resource
    private IUserService userService;


}
