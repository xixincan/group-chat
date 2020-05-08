package com.xxc.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.log.StaticLog;
import com.xxc.common.cache.RedisTool;
import com.xxc.common.consts.RedisKey;
import com.xxc.dao.mapper.GroupMapper;
import com.xxc.dao.mapper.GroupRelationMapper;
import com.xxc.dao.model.Group;
import com.xxc.dao.model.GroupRelation;
import com.xxc.entity.enums.GroupStatusEnum;
import com.xxc.entity.response.GroupInfo;
import com.xxc.entity.response.UserInfo;
import com.xxc.service.IGroupService;
import com.xxc.service.IUserService;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xixincan
 * 2020-03-14
 * @version 1.0.0
 */
@Service
public class GroupService implements IGroupService {

    @Resource
    private RedisTool redisTool;
    @Resource
    private IUserService userService;
    @Resource
    private GroupMapper groupMapper;
    @Resource
    private GroupRelationMapper groupRelationMapper;

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
                        .setOwner(item.getOwner())
                        .setMembers(this.findGroupMembers(item.getId()))
                        .setCreated(DateUtil.formatDateTime(item.getCreated()))
                )
        );

        return groupInfoList;
    }

    private String getKey(Integer gid) {
        return RedisKey.GROUP_INFO_DIR + gid;
    }

    @Override
    public GroupInfo getGroupInfo(Integer gid) {
        if (null == gid || gid <= 0) {
            return null;
        }
        String gInfoKey = this.getKey(gid);
        GroupInfo groupInfo = this.redisTool.serializeGet(gInfoKey, GroupInfo.class);
        if (null == groupInfo) {
            StaticLog.info("群信息缓存未命中，查询DB; gid={}", gid);
            Example example = new Example(Group.class);
            example.createCriteria().andEqualTo("id", gid).andEqualTo("status", GroupStatusEnum.NORMAL.getStatus());
            List<Group> groupList = this.groupMapper.selectByExample(example);
            if (CollectionUtil.isEmpty(groupList)) {
                return null;
            }
            Group group = groupList.get(0);
            groupInfo = new GroupInfo();
            groupInfo.setGroupId(gid)
                    .setGroupName(group.getName())
                    .setGroupAvatarUrl(group.getAvatar())
                    .setOwner(group.getOwner())
                    .setMembers(this.findGroupMembers(gid))
                    .setCreated(DateUtil.formatDateTime(group.getCreated()));
            this.redisTool.serializeSave(gInfoKey, groupInfo, 15 * 24 * 60 * 60);
        }
        return groupInfo;
    }

    @Override
    public List<UserInfo> findGroupMembers(Integer gid) {
        if (null == gid || gid <= 0) {
            return null;
        }
        String gInfoKey = this.getKey(gid);
        GroupInfo groupInfo = this.redisTool.serializeGet(gInfoKey, GroupInfo.class);
        if (null != groupInfo) {
            return groupInfo.getMembers();
        }
        Example example = new Example(GroupRelation.class);
        example.selectProperties("uid");
        example.createCriteria().andEqualTo("gid", gid).andEqualTo("valid", Boolean.TRUE);
        List<GroupRelation> groupRelations = this.groupRelationMapper.selectByExample(example);
        List<String> uidList = groupRelations.stream().map(GroupRelation::getUid).collect(Collectors.toList());
        return this.userService.getUserSimpleInfoList(uidList);
    }

    @Override
    public int addGroup(GroupRelation groupRelation) {
        return this.groupRelationMapper.insertSelective(groupRelation);
    }
}
