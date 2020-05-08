package com.xxc.service;

import com.xxc.dao.model.GroupRelation;
import com.xxc.entity.response.GroupInfo;
import com.xxc.entity.response.UserInfo;

import java.util.List;

/**
 * @author xixincan
 * 2020-03-14
 * @version 1.0.0
 */
public interface IGroupService {

    List<GroupInfo> findGroups(String uid);

    GroupInfo getGroupInfo(Integer gid);

    List<UserInfo> findGroupMembers(Integer gid);

    int addGroup(GroupRelation groupRelation);
}
