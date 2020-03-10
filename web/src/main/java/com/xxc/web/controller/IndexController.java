package com.xxc.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author xixincan
 * 2020-03-10
 * @version 1.0.0
 */
@Controller
@RequestMapping({"/", "/index"})
public class IndexController {

    @RequestMapping(method = RequestMethod.GET)
    public String index() {
        return "/view/index.html";
    }

}
