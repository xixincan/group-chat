package com.xxc.web.controller;

import com.xxc.entity.annotation.SkipLoginCheck;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author xixincan
 * 2020-03-10
 * @version 1.0.0
 */
@Controller
@RequestMapping("")
public class IndexController {
    @SkipLoginCheck
    @RequestMapping(value = {"/", "/index"}, method = RequestMethod.GET)
    public String index() {
        return "/view/index.html";
    }

    @SkipLoginCheck
    @RequestMapping(value = "login", method = RequestMethod.GET)
    public String login() {
        return "/view/login.html";
    }

}
