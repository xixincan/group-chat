package com.xxc.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.xxc.common.enums.UserEventEnum;
import com.xxc.common.enums.UserStatusEnum;
import com.xxc.common.util.EncryptUtil;
import com.xxc.common.util.MyIPUtil;
import com.xxc.dao.mapper.UserLogMapper;
import com.xxc.dao.mapper.UserMapper;
import com.xxc.dao.model.User;
import com.xxc.dao.model.UserLog;
import com.xxc.service.IUserService;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * @author xixincan
 * 2020-03-10
 * @version 1.0.0
 */
@Service
public class UserService implements IUserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private UserLogMapper userLogMapper;

    /**
     * user
     *
     * @param username username
     * @return user
     */
    @Override
    public User getUser(String username) {
        Example example = new Example(User.class);
        example.createCriteria()
                .andEqualTo("username", username)
                .andIn("status", Arrays.asList(UserStatusEnum.FREEZE.getStatus(), UserStatusEnum.NORMAL.getStatus()));
        List<User> users = this.userMapper.selectByExample(example);
        if (CollectionUtil.isNotEmpty(users)) {
            return users.get(0);
        }
        return null;
    }

    /**
     * 记录
     *
     * @param uid     uid
     * @param request req
     * @param event   ev
     */
    @Override
    public void userLog(String uid, HttpServletRequest request, UserEventEnum event) {
        String ipAddr = MyIPUtil.getRemoteIpAddr(request);
        UserLog userLog = new UserLog();
        userLog.setUid(uid);
        userLog.setEvent(event.getEvent());
        userLog.setIpAddr(ipAddr);
        this.userLogMapper.insertSelective(userLog);
    }

}
