package com.xxc.service.impl;

import cn.hutool.log.StaticLog;
import com.xxc.dao.mapper.GroupRelationMapper;
import com.xxc.dao.mapper.UserMapper;
import com.xxc.dao.mapper.UserRelationMapper;
import com.xxc.dao.model.GroupRelation;
import com.xxc.dao.model.User;
import com.xxc.dao.model.UserRelation;
import com.xxc.entity.exp.BizException;
import com.xxc.service.ITranService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author xixincan
 * 2020-05-08
 * @version 1.0.0
 */
@Service
public class TranService implements ITranService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private UserRelationMapper userRelationMapper;
    @Resource
    private GroupRelationMapper groupRelationMapper;

    @Override
    public void transGet(String uid) {
        Example example = new Example(User.class);
        example.createCriteria().andEqualTo("uid", uid);
        List<User> users = this.userMapper.selectByExample(example);
        if (users.size() <= 0) {
            StaticLog.warn("没有找到uid={}的用户", uid);
            return;
        }
        example.clear();
        example = new Example(UserRelation.class);
        example.createCriteria().andEqualTo("uid", uid);
        List<UserRelation> userRelations = this.userRelationMapper.selectByExample(example);
        if (userRelations.size() <= 0) {
            StaticLog.warn("没有找到uid={}的用关系", uid);
        }
        example.clear();
        ;
        example = new Example(GroupRelation.class);
        example.createCriteria().andEqualTo("uid", uid);
        List<GroupRelation> groupRelations = this.groupRelationMapper.selectByExample(example);
        if (groupRelations.size() <= 0) {
            StaticLog.warn("没有找到uid={}的用户组关系", uid);
        }
        StaticLog.info("-----transGet查询完毕-------");
    }

    static ExecutorService service = new ThreadPoolExecutor(1, 2, 5, TimeUnit.SECONDS, new LinkedBlockingQueue<>(128));

    @Override
    public void transGetAsync(String uid) {
        Example example = new Example(User.class);
        example.createCriteria().andEqualTo("uid", uid);
        List<User> users = this.userMapper.selectByExample(example);
        if (users.size() <= 0) {
            StaticLog.warn("async没有找到uid={}的用户", uid);
            throw new BizException("none");
        }

        CompletableFuture<Void> userF = CompletableFuture.runAsync(() -> {
            Example example1 = new Example(UserRelation.class);
            example1.createCriteria().andEqualTo("uid", uid);
            List<UserRelation> userRelations = userRelationMapper.selectByExample(example1);
            if (userRelations.size() <= 0) {
                StaticLog.warn("async没有找到uid={}的用关系", uid);
            }
        }, service);

        CompletableFuture<Void> groupF = CompletableFuture.runAsync(() -> {
            Example example1 = new Example(GroupRelation.class);
            example1.createCriteria().andEqualTo("uid", uid);
            List<GroupRelation> groupRelations = this.groupRelationMapper.selectByExample(example1);
            if (groupRelations.size() <= 0) {
                StaticLog.warn("async没有找到uid={}的用户组关系", uid);
            }
        }, service);
        CompletableFuture.allOf(userF, groupF).join();
        StaticLog.info("======async end======");
    }
}
