package com.xxc.test;

import cn.hutool.json.JSONUtil;
import com.xxc.common.cache.RedisService;
import com.xxc.common.consts.ConfigKey;
import com.xxc.common.util.SerializeUtil;
import com.xxc.dao.model.User;
import com.xxc.entity.response.UserInfo;
import com.xxc.service.IConfigService;
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
    private IConfigService configService;
    @Autowired
    UserService userService;

    @Test
    public void testRedis() {

        this.redisService.setString("ok", "okv", 300);
        this.redisService.setInteger("123", 123, 300);
        System.out.println(this.redisService.getString("ok"));
        System.out.println(this.redisService.getInteger("123"));

        User user = new User();
        user.setNickname("nknknk");
        user.setId(1);
        user.setPassword("pwpwpwpw");
        user.setMailbox("shjbshfshs2");
        System.out.println(JSONUtil.toJsonStr(user));
        this.redisService.setString("user:user", JSONUtil.toJsonStr(user), 300);
        this.redisService.serializeSave("uuu", user, 300);
        User user1 = this.redisService.serializeGet("uuu", User.class);
        System.out.println(JSONUtil.toJsonStr(user1));

    }

    @Test
    public void testUserInfo() {
//        UserInfo xixincan = this.userService.getUserInfo("xixincan");
//        System.out.println(JSONUtil.toJsonStr(xixincan));
    }

    @Test
    public void tt() {
        System.out.println(this.configService.getValue(ConfigKey.CHAT_WS_PORT));
    }

}
