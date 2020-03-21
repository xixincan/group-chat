package com.xxc.web.controller;

import com.xxc.entity.annotation.SkipLoginCheck;
import com.xxc.entity.exp.ValidException;
import com.xxc.entity.request.UserRegisterForm;
import com.xxc.entity.response.UserInfo;
import com.xxc.entity.result.MyResult;
import com.xxc.service.IUserService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

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
    public MyResult<UserInfo> getUserInfo(HttpServletRequest request) {
        return MyResult.success(this.userService.getSelfUserInfo(request));
    }

    @SkipLoginCheck
    @PostMapping("register")
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

    @PostMapping("fuzzy/search")
    public MyResult<List<UserInfo>> search(String keyword) {
        return MyResult.success(this.userService.search(keyword));
    }

    @PostMapping("build/friend")
    public MyResult<Boolean> buildFriend(HttpServletRequest request, String fuid) {
        return MyResult.success(this.userService.buildRelation(request, fuid));
    }
}
