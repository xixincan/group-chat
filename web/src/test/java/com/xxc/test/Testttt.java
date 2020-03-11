package com.xxc.test;

import cn.hutool.json.JSONUtil;
import com.xxc.common.cache.RedisService;
import com.xxc.entity.response.UserInfo;
import com.xxc.service.impl.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author: xixincan
 * @Date: 2019-05-20
 * @Version 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class Testttt {

    @Autowired
    private RedisService redisService;

    @Autowired
    UserService userService;

    @Test
    public void testRedis() {
        System.out.println(this.redisService.getString("test"));
        System.out.println(this.redisService.getString("test1"));
        System.out.println(this.redisService.getString("test2"));
        System.out.println(this.redisService.getString("test3"));
        System.out.println(this.redisService.getString("ok"));
        System.out.println(this.redisService.getString("ok1"));
        System.out.println(this.redisService.getString("ok2"));
        System.out.println(this.redisService.getString("ok3"));
    }

    @Test
    public void testUserInfo() {
//        UserInfo xixincan = this.userService.getUserInfo("xixincan");
//        System.out.println(JSONUtil.toJsonStr(xixincan));
    }

}
