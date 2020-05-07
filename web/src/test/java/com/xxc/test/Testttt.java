package com.xxc.test;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.xxc.common.cache.RedisTool;
import com.xxc.common.consts.ConfigKey;
import com.xxc.dao.mapper.MsgLogMapper;
import com.xxc.dao.mapper.UserMsgMapper;
import com.xxc.dao.model.MsgLog;
import com.xxc.dao.model.User;
import com.xxc.dao.model.UserMsg;
import com.xxc.service.IConfigService;
import com.xxc.service.impl.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: xixincan
 * @Date: 2019-05-20
 * @Version 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class Testttt {

    @Autowired
    private RedisTool redisTool;
    @Autowired
    private IConfigService configService;
    @Autowired
    UserService userService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private MsgLogMapper msgLogMapper;
    @Resource
    private UserMsgMapper userMsgMapper;


    @Test
    public void y() {
        Long demo = this.redisTemplate.opsForValue().increment("demo", 1);
        System.out.println(demo);
        this.redisTool.remove("demo");
    }

    @Test
    public void testRedis() {

        this.redisTool.setString("ok", "okv", 300);
        this.redisTool.setInteger("123", 123, 300);
        System.out.println(this.redisTool.getString("ok"));
        System.out.println(this.redisTool.getInteger("123"));

        User user = new User();
        user.setNickname("nknknk");
        user.setId(1);
        user.setPassword("pwpwpwpw");
        user.setMailbox("shjbshfshs2");
        System.out.println(JSONUtil.toJsonStr(user));
        this.redisTool.setString("user:user", JSONUtil.toJsonStr(user), 300);
        this.redisTool.serializeSave("uuu", user, 300);
        User user1 = this.redisTool.serializeGet("uuu", User.class);
        System.out.println(JSONUtil.toJsonStr(user1));

    }

    @Test
    public void ggg() {
        Example example = new Example(MsgLog.class);
        example.setTableName("chat_log_0");
        example.createCriteria().andEqualTo("uid", "wqw").andEqualTo("gid", 2);
        MsgLog chatLog = new MsgLog();
        chatLog.setSourceUid("yuu");
        chatLog.setGid(2);
//        List<MsgLog> chatLogs = this.msgLogMapper.select(chatLog);
        List<MsgLog> chatLogs = this.msgLogMapper.selectByExample(example);
        System.out.println(JSONUtil.toJsonStr(chatLogs));
    }

    @Test
    public void tt() {
        System.out.println(this.configService.getValue(ConfigKey.CHAT_WS_PORT));
    }

    @Test
    public void ll() {
        List<UserMsg> list = new ArrayList<>();
        UserMsg userMsg = new UserMsg();
        userMsg.setMid("qwqw");
        userMsg.setSent(Boolean.TRUE);
        userMsg.setId(null);
        userMsg.setCreated(null);
        userMsg.setUid("123");
        this.userMsgMapper.insertSelective(userMsg);
        list.add(userMsg);
        userMsg = new UserMsg();
        userMsg.setMid("qwqw111");
        userMsg.setSent(Boolean.FALSE);
        userMsg.setId(null);
        userMsg.setCreated(null);
        userMsg.setUid("12311");
        this.userMsgMapper.insertSelective(userMsg);
        this.userMsgMapper.insertList(list); //error
    }

    @Test
    public void sfa() {
        User admin = this.userService.getUser("admin");
        System.out.println(JSON.toJSONString(admin));
        System.out.println(admin.getAge());
        this.userService.addAge(admin.getId().longValue());
        System.out.println(JSON.toJSONString(admin));
        System.out.println(admin.getAge());
    }


}
