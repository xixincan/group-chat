package com.xxc.web.controller;

import com.xxc.entity.exp.ValidException;
import com.xxc.entity.request.UserRegisterForm;
import com.xxc.entity.response.UserInfo;
import com.xxc.entity.result.MyResult;
import com.xxc.service.IUserService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

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
        return MyResult.success(this.userService.getSelfUserInfo(request));
    }

    @PostMapping("register")
    @ResponseBody
    public MyResult<String> register(HttpServletRequest request,
                                     HttpServletResponse response,
                                     @Valid UserRegisterForm registerForm,
                                     BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return MyResult.error(9999, "注册信息不完整", new ValidException("信息不完整"));
        }
        this.userService.register(request, response, registerForm);
        return MyResult.success("/");
    }
}
