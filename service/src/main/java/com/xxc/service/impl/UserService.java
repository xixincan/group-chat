package com.xxc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import com.xxc.common.cache.RedisService;
import com.xxc.common.consts.ConfigKey;
import com.xxc.common.consts.RedisKey;
import com.xxc.common.util.TicketUtil;
import com.xxc.dao.model.*;
import com.xxc.entity.enums.GroupStatusEnum;
import com.xxc.entity.enums.UserEventEnum;
import com.xxc.entity.enums.UserStatusEnum;
import com.xxc.common.util.MyIPUtil;
import com.xxc.dao.mapper.*;
import com.xxc.entity.exp.AccessException;
import com.xxc.entity.response.GroupInfo;
import com.xxc.entity.response.UserInfo;
import com.xxc.service.IUserService;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
    private RedisService redisService;
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserRelationMapper userRelationMapper;
    @Resource
    private UserLogMapper userLogMapper;
    @Resource
    private GroupMapper groupMapper;
    @Resource
    private GroupRelationMapper groupRelationMapper;

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
    public UserInfo getUserInfo(HttpServletRequest request) {
        //ticket -> uid
        String ticket = TicketUtil.getTicket(request);
        if (StrUtil.isEmpty(ticket)) {
            throw new AccessException("请重新登录");
        }
        String uid = TicketUtil.getUid(ticket);
        String userKey = RedisKey.USER_KEY + uid;
        User cacheUser = this.redisService.serializeGet(userKey, User.class);
        if (null == cacheUser) {
            throw new AccessException("请重新登录");
        }

        final String userInfoKey = RedisKey.USER_INFO_KEY + uid;
        UserInfo cacheInfo = this.redisService.serializeGet(userInfoKey, UserInfo.class);

        if (null == cacheInfo) {
            StaticLog.info("用户详情缓存未命中:{}", userInfoKey);
            final UserInfo userInfo = new UserInfo();
            BeanUtil.copyProperties(cacheUser, userInfo);
            userInfo.setGroupList(this.findGroups(userInfo.getUid()));
            userInfo.setFriendList(this.findFriends(userInfo.getUid()));
            CompletableFuture
                    .runAsync(() -> this.redisService.serializeSave(userInfoKey, userInfo, 7 * 24 * 60 * 60))
                    .thenRunAsync(() -> StaticLog.info("用户详情已经写入缓存:{}", userInfoKey));

            return userInfo;
        }
        return cacheInfo;
    }

    @Override
    public List<UserInfo> findFriends(String uid) {
        Example example = new Example(UserRelation.class);
        example.selectProperties("uid", "fuid");
        example.createCriteria().andEqualTo("uid", uid).orEqualTo("fuid", uid);
        List<UserRelation> relationList = this.userRelationMapper.selectByExample(example);
        Set<String> uidSet = relationList.stream().map(UserRelation::getFuid).collect(Collectors.toSet());
        uidSet.addAll(relationList.stream().map(UserRelation::getUid).collect(Collectors.toSet()));
        uidSet.remove(uid);
        return this.getUserSimpleInfoList(uidSet);
//        example.clear();
//        example.createCriteria().andEqualTo("fuid", uid);
//        List<UserRelation> relationListR = this.userRelationMapper.selectByExample(example);
//        uidSet.addAll(relationListR.stream().map(UserRelation::getUid).collect(Collectors.toList()));
//        return this.getUserSimpleInfoList(uidSet);
    }

    @Override
    public List<GroupInfo> findGroups(String uid) {
        Example example = new Example(GroupRelation.class);
        example.selectProperties("gid");
        example.createCriteria().andEqualTo("uid", uid).andEqualTo("valid", Boolean.TRUE);
        List<GroupRelation> groupRelationList = this.groupRelationMapper.selectByExample(example);
        List<GroupInfo> groupInfoList = new ArrayList<>();
        if (CollectionUtil.isEmpty(groupRelationList)) {
            return groupInfoList;
        }

        List<Integer> gidList = groupRelationList.stream().map(GroupRelation::getGid).collect(Collectors.toList());
        example = new Example(Group.class);
        example.createCriteria().andIn("id", gidList).andEqualTo("status", GroupStatusEnum.NORMAL.getStatus());
        List<Group> groupList = this.groupMapper.selectByExample(example);
        groupList.forEach(item -> groupInfoList.add(
                new GroupInfo()
                        .setGroupId(item.getId())
                        .setGroupName(item.getName())
                        .setGroupAvatarUrl(item.getAvatar())
                        .setMembers(this.findMembers(item.getId()))
                )
        );

        return groupInfoList;
    }

    @Override
    public List<UserInfo> findMembers(Integer gid) {
        Example example = new Example(GroupRelation.class);
        example.selectProperties("uid");
        example.createCriteria().andEqualTo("gid", gid).andEqualTo("valid", Boolean.TRUE);
        List<GroupRelation> groupRelations = this.groupRelationMapper.selectByExample(example);
        List<String> uidList = groupRelations.stream().map(GroupRelation::getUid).collect(Collectors.toList());
        return this.getUserSimpleInfoList(uidList);
    }

    @Override
    public List<UserInfo> getUserSimpleInfoList(Collection<String> uidList) {
        //todo 优化-> 先从缓存中取user转换成UserInfo
        Example example = new Example(User.class);
        example.selectProperties("uid", "nickname", "avatar", "age", "sex");
        example.createCriteria().andIn("uid", uidList).andEqualTo("status", UserStatusEnum.NORMAL.getStatus());
        List<User> userList = this.userMapper.selectByExample(example);
        List<UserInfo> userSimpleInfoList = new ArrayList<>();
        userList.forEach(item ->
                userSimpleInfoList.add(new UserInfo()
                        .setUid(item.getUid())
                        .setNickname(item.getNickname())
                        .setAvatar(item.getAvatar())
                        .setAge(item.getAge())
                        .setSex(item.getSex()))
        );
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
