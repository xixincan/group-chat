package com.xxc.web.controller;

import com.xxc.entity.annotation.SkipLoginCheck;
import com.xxc.entity.request.UserLoginForm;
import com.xxc.entity.result.MyResult;
import com.xxc.service.ILoginService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @author xixincan
 * 2020-03-10
 * @version 1.0.0
 */
@Controller
@RequestMapping("")
public class IndexController {

    @Resource
    private ILoginService loginService;

    @RequestMapping(value = {"/"}, method = RequestMethod.GET)
    public String index() {
        return "/index.html";
    }

    @SkipLoginCheck
    @ResponseBody
    @PostMapping("login")
    public MyResult<String> login(@Valid UserLoginForm userLoginForm,
                                  BindingResult result,
                                  HttpServletRequest request, HttpServletResponse response) {
        if (result.hasErrors()) {
            return new MyResult<>(500, result.getAllErrors().get(0).getDefaultMessage());
        }
        this.loginService.doLogin(request, response, userLoginForm);
        return new MyResult<>("/");
    }

    @ResponseBody
    @PostMapping("logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        this.loginService.logout(request, response);
        return "/login.html";
    }

}
