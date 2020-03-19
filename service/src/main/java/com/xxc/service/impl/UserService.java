package com.xxc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import com.xxc.common.cache.RedisTool;
import com.xxc.common.consts.RedisKey;
import com.xxc.common.util.EncryptUtil;
import com.xxc.common.util.MyTicketUtil;
import com.xxc.dao.model.*;
import com.xxc.entity.enums.UserEventEnum;
import com.xxc.entity.enums.UserStatusEnum;
import com.xxc.common.util.MyIPUtil;
import com.xxc.dao.mapper.*;
import com.xxc.entity.exp.AccessException;
import com.xxc.entity.request.UserLoginForm;
import com.xxc.entity.request.UserRegisterForm;
import com.xxc.entity.response.UserInfo;
import com.xxc.service.IGroupService;
import com.xxc.service.ILoginService;
import com.xxc.service.IUserService;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author xixincan
 * 2020-03-10
 * @version 1.0.0
 */
@Service
public class UserService implements IUserService {

    @Resource
    private RedisTool redisTool;
    @Resource
    private IGroupService groupService;
    @Resource
    private ILoginService loginService;
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserRelationMapper userRelationMapper;
    @Resource
    private UserLogMapper userLogMapper;

    @Override
    public User register(HttpServletRequest request, HttpServletResponse response, UserRegisterForm registerForm) {
        //todo 检测非法字符
        Example example = new Example(User.class);
        example.createCriteria().andEqualTo("username", registerForm.getUsername());
        if (this.userMapper.selectCountByExample(example) > 0) {
            StaticLog.error("用户名[{}]已经存在", registerForm.getUsername());
            throw new AccessException("用户名重复");
        }
        String uid = EncryptUtil.encodeBase64(registerForm.getUsername());
        User newUser = new User();
        newUser.setUid(uid);
        newUser.setUsername(registerForm.getUsername());
        newUser.setPassword(registerForm.getPassword());
        newUser.setNickname(registerForm.getNickname());
        newUser.setMailbox(registerForm.getMailbox());
        newUser.setAddress(registerForm.getAddress());
        newUser.setMobile(registerForm.getMobile());
        this.userMapper.insertSelective(newUser);
        StaticLog.info("新用户注册完毕::UID={}", uid);
        this.loginService.doLogin(request, response, new UserLoginForm(newUser.getUsername(), newUser.getPassword()));
        return newUser;
    }

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

    @Override
    public UserInfo getSelfUserInfo(HttpServletRequest request) {
        //ticket -> uid
        String ticket = MyTicketUtil.getTicket(request);
        if (StrUtil.isEmpty(ticket)) {
            throw new AccessException("请重新登录");
        }
        String uid = MyTicketUtil.getUid(ticket);
        String userKey = RedisKey.USER_DIR + uid;
        User cacheUser = this.redisTool.serializeGet(userKey, User.class);
        if (null == cacheUser) {
            throw new AccessException("请重新登录");
        }

        final String userInfoKey = RedisKey.USER_INFO_DIR + uid;
        UserInfo cacheInfo = this.redisTool.serializeGet(userInfoKey, UserInfo.class);

        if (null == cacheInfo) {
            StaticLog.info("用户详情缓存未命中:{}", userInfoKey);
            final UserInfo userInfo = new UserInfo();
            BeanUtil.copyProperties(cacheUser, userInfo);
            userInfo.setGroupList(this.groupService.findGroups(userInfo.getUid()));
            userInfo.setFriendList(this.findFriends(userInfo.getUid()));
            CompletableFuture
                    .runAsync(() -> this.redisTool.serializeSave(userInfoKey, userInfo, 7 * 24 * 60 * 60))
                    .thenRunAsync(() -> StaticLog.info("用户详情已经写入缓存:{}", userInfoKey));

            return userInfo;
        }
        return cacheInfo;
    }

    private List<UserInfo> findFriends(String uid) {
        Example example = new Example(UserRelation.class);
        example.selectProperties("uid", "fuid");
        Example.Criteria criteria = example.createCriteria().andEqualTo("uid", uid).andEqualTo("valid", Boolean.TRUE);
        example.createCriteria().andEqualTo("fuid", uid).andEqualTo("valid", Boolean.TRUE);
        example.or(criteria);
        List<UserRelation> relationList = this.userRelationMapper.selectByExample(example);
        Set<String> uidSet = relationList.stream()
                .map(UserRelation::getFuid)
                .collect(Collectors.toSet());
        uidSet.addAll(relationList.stream()
                .map(UserRelation::getUid)
                .collect(Collectors.toSet()));
        uidSet.remove(uid);
        return this.getUserSimpleInfoList(uidSet);
//        example.clear();
//        example.createCriteria().andEqualTo("fuid", uid);
//        List<UserRelation> relationListR = this.userRelationMapper.selectByExample(example);
//        uidSet.addAll(relationListR.stream().map(UserRelation::getUid).collect(Collectors.toList()));
//        return this.getUserSimpleInfoList(uidSet);
    }

    /**
     * 检查是否为正常用用户
     *
     * @param uid
     */
    @Override
    public Boolean checkUser(String uid) {
        String infoKey = RedisKey.USER_INFO_DIR + uid;
        UserInfo cacheInfo = this.redisTool.serializeGet(infoKey, UserInfo.class);
        if (null != cacheInfo) {
            return true;
        }
        Example example = new Example(User.class);
        example.createCriteria().andEqualTo("uid", uid).andEqualTo("status", UserStatusEnum.NORMAL.getStatus());
        return this.userMapper.selectCountByExample(example) > 0;
    }

    /**
     * 获取用户简要信息
     */
    @Override
    public UserInfo getUserSimpleInfo(String uid) {
        String infoKey = RedisKey.USER_INFO_DIR + uid;
        UserInfo result = new UserInfo();
        UserInfo cacheInfo = this.redisTool.serializeGet(infoKey, UserInfo.class);
        if (null == cacheInfo) {
            Example example = new Example(User.class);
            example.selectProperties("uid", "nickname", "avatar", "age", "sex");
            example.createCriteria().andEqualTo("uid", uid).andEqualTo("status", UserStatusEnum.NORMAL.getStatus());
            List<User> userList = this.userMapper.selectByExample(example);
            if (CollectionUtil.isEmpty(userList)) {
                return null;
            }
            User user = userList.get(0);
            return result.setUid(uid)
                    .setNickname(user.getNickname())
                    .setAvatar(user.getAvatar())
                    .setSex(user.getSex())
                    .setAge(user.getAge());
        }
        return result.setUid(uid)
                .setNickname(cacheInfo.getNickname())
                .setAvatar(cacheInfo.getAvatar())
                .setAge(cacheInfo.getAge())
                .setSex(cacheInfo.getSex());
    }

    @Override
    public List<UserInfo> getUserSimpleInfoList(Collection<String> uidList) {
        List<UserInfo> userSimpleInfoList = new ArrayList<>();
        Example example = new Example(User.class);
        example.selectProperties("uid", "nickname", "avatar", "age", "sex");
        example.createCriteria().andIn("uid", uidList).andEqualTo("status", UserStatusEnum.NORMAL.getStatus());
        List<User> userList = this.userMapper.selectByExample(example);
        if (CollectionUtil.isNotEmpty(userList)) {
            userList.forEach(item ->
                    userSimpleInfoList.add(new UserInfo()
                            .setUid(item.getUid())
                            .setNickname(item.getNickname())
                            .setAvatar(item.getAvatar())
                            .setAge(item.getAge())
                            .setSex(item.getSex()))
            );
        }
        return userSimpleInfoList;
    }

    @Override
    public void recordUserLog(String uid, HttpServletRequest request, UserEventEnum event) {
        String ipAddr = MyIPUtil.getRemoteIpAddr(request);
        UserLog userLog = new UserLog();
        userLog.setUid(uid);
        userLog.setEvent(event.getEvent());
        userLog.setIpAddr(ipAddr);
        this.userLogMapper.insertSelective(userLog);
    }

}
