package com.xxc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import com.github.pagehelper.PageHelper;
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
import com.xxc.entity.exp.BizException;
import com.xxc.entity.request.UserLoginForm;
import com.xxc.entity.request.UserRegisterForm;
import com.xxc.entity.response.UserInfo;
import com.xxc.service.IGroupService;
import com.xxc.service.ILoginService;
import com.xxc.service.ITranService;
import com.xxc.service.IUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
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
        example.createCriteria().andEqualTo("uid", uid).andEqualTo("valid", Boolean.TRUE);
        Example.Criteria criteria = example.createCriteria().andEqualTo("fuid", uid).andEqualTo("valid", Boolean.TRUE);
        example.or(criteria);
        List<UserRelation> relationList = this.userRelationMapper.selectByExample(example);
        Set<String> uidSet = relationList.stream()
                .map(UserRelation::getFuid)
                .collect(Collectors.toSet());
        uidSet.addAll(relationList.stream()
                .map(UserRelation::getUid)
                .collect(Collectors.toSet()));
        uidSet.remove(uid);
        if (CollectionUtil.isEmpty(uidSet)) {
            return new ArrayList<>();
        }
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
        if (CollectionUtil.isNotEmpty(uidList)) {
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

    @Override
    public List<UserInfo> search(String keyword) {
        final ArrayList<UserInfo> resultList = new ArrayList<>();
        if (StrUtil.isEmpty(keyword)) {
            return resultList;
        }
        String fuzzy = "%" + keyword + "%";
        Example example = new Example(User.class);
        example.selectProperties("uid", "username", "nickname", "avatar");
        Example.Criteria criteria = example.createCriteria().andEqualTo("status", UserStatusEnum.NORMAL.getStatus());
        example.createCriteria().andLike("nickname", fuzzy).orLike("username", fuzzy);
        example.and(criteria);
        //limit 10
        PageHelper.startPage(1, 10);
        List<User> userList = this.userMapper.selectByExample(example);
        if (CollectionUtil.isEmpty(userList)) {
            return resultList;
        }
        userList.forEach(item -> {
            UserInfo userInfo = new UserInfo();
            userInfo.setUid(item.getUid());
            userInfo.setNickname(item.getNickname());
            userInfo.setAvatar(item.getAvatar());
            resultList.add(userInfo);
        });
        return resultList;
    }

    @Override
    public Boolean buildRelation(HttpServletRequest request, String fuid) {
        if (!this.checkUser(fuid)) {
            StaticLog.error("目标用户不存在或者不是正常状态fuid={}", fuid);
            return false;
        }
        String uid = MyTicketUtil.getUid(MyTicketUtil.getTicket(request));
        UserRelation relation = new UserRelation();
        relation.setUid(uid);
        relation.setFuid(fuid);
        relation.setValid(Boolean.TRUE);
        this.userRelationMapper.insertSelective(relation);
        StaticLog.info("用户[{}]添加好友[{}]成功", uid, fuid);
        return Boolean.TRUE;
    }

    @Override
    public Boolean destroyRelation(HttpServletRequest request, String fuid) {
        String uid = MyTicketUtil.getUid(MyTicketUtil.getTicket(request));
        Example example = new Example(UserRelation.class);
        example.selectProperties("id");
        Example.Criteria criteria = example.createCriteria().andEqualTo("uid", uid).andEqualTo("fuid", fuid).andEqualTo("valid", Boolean.TRUE);
        example.createCriteria().andEqualTo("fuid", uid).andEqualTo("uid", fuid).andEqualTo("valid", Boolean.TRUE);
        example.or(criteria);
        List<UserRelation> relationList = this.userRelationMapper.selectByExample(example);
        if (CollectionUtil.isNotEmpty(relationList)) {
            relationList.forEach(item -> {
                item.setValid(Boolean.FALSE);
                this.userRelationMapper.updateByPrimaryKeySelective(item);
            });
            StaticLog.info("[{}]-[{}]好友关系解除成功", uid, fuid);
        } else {
            StaticLog.warn("没有找到[{}]-[{}]好友关系", uid, fuid);
        }
        return Boolean.TRUE;
    }

    public void addAge(Long id) {
        this.userMapper.addAge(id);
    }

    @Resource
    private ITranService tranService;
    private String uid;
    @Override
    @Transactional(isolation = Isolation.READ_UNCOMMITTED, propagation = Propagation.REQUIRES_NEW)
    public Boolean testTransaction(UserRegisterForm registerForm) {
        User user =  new User();
        user.setUid(EncryptUtil.genRandomID());
        user.setUsername(registerForm.getUsername() + System.currentTimeMillis());
        user.setPassword(registerForm.getPassword());
        user.setNickname(registerForm.getNickname());
        user.setAvatar(registerForm.getAddress());
        user.setMailbox(registerForm.getMailbox());
        this.addUser(user);
        this.addUserRelation(user.getUid());
        this.addGroupRelation(user.getUid());
        uid = user.getUid();
        try {
            Thread.sleep(20000L);
        } catch (InterruptedException e) {
            StaticLog.error(e);
        }
        this.tranService.transGet(user.getUid());
        tranService.transGetAsync(user.getUid());
//        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
//            @Override
//            public void afterCommit() {
//                tranService.transGetAsync(user.getUid());
//            }
//        });

        return Boolean.TRUE;
    }

    @Override
    @Transactional(isolation = Isolation.READ_UNCOMMITTED, propagation = Propagation.REQUIRES_NEW)
    public User testTransactionGet(String uid) {
        this.tranService.transGet(this.uid);
        tranService.transGetAsync(this.uid);
        return null;
    }

    private void addGroupRelation(String uid) {
        GroupRelation groupRelation = new GroupRelation();
        groupRelation.setGid(new Random().nextInt(100));
        groupRelation.setUid(uid);
        groupRelation.setValid(Boolean.TRUE);
        int i = this.groupService.addGroup(groupRelation);
        if (i > 0) {
            StaticLog.info("插入组关系成功");
        } else {
            StaticLog.error("插入组关系失败");
            throw new BizException("插入组关系失败");
        }
    }

    private void addUserRelation(String uid) {
        UserRelation userRelation = new UserRelation();
        userRelation.setValid(Boolean.TRUE);
        userRelation.setUid(uid);
        userRelation.setFuid(EncryptUtil.genRandomID());
        int i = this.userRelationMapper.insertSelective(userRelation);
        if (i > 0) {
            StaticLog.info("插入用户关系成功");
        } else {
            StaticLog.error("插入用户关系失败");
            throw new BizException("插入用户关系失败");
        }
    }

    private void addUser(User user) {
        int i = this.userMapper.insertSelective(user);
        if (i <= 0) {
            StaticLog.error("保存用户失败");
            throw new BizException("保存用户失败");
        } else {
            StaticLog.info("保存用户成功");
        }
    }
}
