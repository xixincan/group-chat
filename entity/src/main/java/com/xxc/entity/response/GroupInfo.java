package com.xxc.entity.response;

import java.io.Serializable;
import java.util.List;

/**
 * @author xixincan
 * 2020-03-11
 * @version 1.0.0
 */
public class GroupInfo implements Serializable {

    private Integer groupId;
    private String groupName;
    private String groupAvatarUrl;
    private List<UserInfo> members;

    public Integer getGroupId() {
        return groupId;
    }

    public GroupInfo setGroupId(Integer groupId) {
        this.groupId = groupId;
        return this;
    }

    public String getGroupName() {
        return groupName;
    }

    public GroupInfo setGroupName(String groupName) {
        this.groupName = groupName;
        return this;
    }

    public String getGroupAvatarUrl() {
        return groupAvatarUrl;
    }

    public GroupInfo setGroupAvatarUrl(String groupAvatarUrl) {
        this.groupAvatarUrl = groupAvatarUrl;
        return this;
    }

    public List<UserInfo> getMembers() {
        return members;
    }

    public GroupInfo setMembers(List<UserInfo> members) {
        this.members = members;
        return this;
    }
}
