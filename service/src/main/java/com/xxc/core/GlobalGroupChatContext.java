package com.xxc.core;

import cn.hutool.core.util.StrUtil;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.net.SocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 全局的channel管理上下文，用于可扩展的Netty集群中共享channel
 *
 * @author xixincan
 * 2020-03-17
 * @version 1.0.0
 */
@Service
public class GlobalGroupChatContext {

    //本地用户与客户端连接ID的双向注册表 uid <--> channelID
    private static final BiMap<String, String> LOCAL_BINDING_MAP = HashBiMap.create();

    //本地客户端映射表 channelID <--> Channel
    private static final Map<String, Channel> LOCAL_CHANNEL_MAP = new ConcurrentHashMap<>();

    //全局用户与{客户端标识,客户端连接ID}的双向注册表（监听zk事件并随之更新）uid <--> {sid,channelID}
    private static final BiMap<String, Pair<String, String>> CLUSTER_BINDING_MAP = HashBiMap.create();

    public void bind(String uid, Channel channel) {
        //1.本地注册
        if (StrUtil.isEmpty(uid) || null == channel) {
            return;
        }
        String channelId = channel.id().asLongText();
        LOCAL_BINDING_MAP.put(uid, channelId);
        LOCAL_CHANNEL_MAP.put(channelId, channel);
        //2.zk注册
        //TODO

    }

    public String unbind(Channel channel) {
        //1.本地卸载
        if (null == channel) {
            return null;
        }
        String channelId = channel.id().asLongText();
        String removeUid = LOCAL_BINDING_MAP.inverse().remove(channelId);
        LOCAL_CHANNEL_MAP.remove(channelId);
        //2.zk卸载
        //TODO

        return removeUid;
    }

    public boolean isOnLocalBinding(String uid) {
        return LOCAL_BINDING_MAP.containsKey(uid);
    }

    public boolean isOnBinding(String uid) {
        if (LOCAL_BINDING_MAP.containsKey(uid)) {
            return true;
        }
        return CLUSTER_BINDING_MAP.containsKey(uid);
    }

    public String getLocalBindUid(Channel channel) {
        if (null == channel) {
            return null;
        }
        return LOCAL_BINDING_MAP.inverse().get(channel.id().asLongText());
    }

    public Channel getLocalChannel(String targetUid) {
        String channelId = LOCAL_BINDING_MAP.get(targetUid);
        if (null != channelId) {
            return LOCAL_CHANNEL_MAP.get(channelId);
        }
        return null;
    }

    public List<Channel> getLocalChannelGroup(Collection<String> uidCollection) {
        return uidCollection.parallelStream()
                .map(uid -> {
                    String channelId = LOCAL_BINDING_MAP.get(uid);
                    if (null != channelId) {
                        return LOCAL_CHANNEL_MAP.get(channelId);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static void main(String[] args) {
        BiMap<String, String> TMP = HashBiMap.create();
        TMP.forcePut("a", "Aaaaa");
        System.out.println(TMP);
        TMP.forcePut("a", "AAAAA");
        System.out.println(TMP);
        TMP.inverse().remove("AAAAA");
        System.out.println(TMP);
        TMP.inverse().remove("AAAAA");
        System.out.println(TMP);
    }

}
