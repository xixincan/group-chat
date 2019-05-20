package com.xxc.web.controller;

import com.xxc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: xixincan
 * @Date: 2019/5/20
 * @Version 1.0
 */
@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("get")
    public Object get(String name) {
        if (StringUtils.isEmpty(name)) {
            return this.userService.getAll();
        }
        return this.userService.get(name);
    }

    @RequestMapping("page")
    public Object page(@RequestParam(required = false, defaultValue = "1") Integer page,
                       @RequestParam(required = false, defaultValue = "3") Integer size) {
        return this.userService.page(page, size);
    }

}
