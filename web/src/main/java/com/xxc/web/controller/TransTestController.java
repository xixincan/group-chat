package com.xxc.web.controller;

import com.xxc.dao.model.User;
import com.xxc.entity.annotation.SkipLoginCheck;
import com.xxc.entity.request.UserRegisterForm;
import com.xxc.entity.result.MyResult;
import com.xxc.service.IUserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author xixincan
 * 2020-05-08
 * @version 1.0.0
 */
@RestController
@RequestMapping("trans/test")
public class TransTestController {

    @Resource
    private IUserService userService;

    @SkipLoginCheck
    @PostMapping("add")
    @ResponseBody
    public MyResult<Boolean> testTransaction(@RequestBody UserRegisterForm registerForm) {
        return MyResult.success(this.userService.testTransaction(registerForm));
    }

    @SkipLoginCheck
    @GetMapping("get")
    @ResponseBody
    public MyResult<User> testTransaction(String uid) {
        return MyResult.success(this.userService.testTransactionGet(uid));
    }

}
