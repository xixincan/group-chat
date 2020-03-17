package com.xxc.core;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import io.netty.channel.Channel;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 全局的channel管理上下文，用于可扩展的Netty集群中共享channel
 *
 * @author xixincan
 * 2020-03-17
 * @version 1.0.0
 */
@Service
public class GlobalGroupChatContext {

    //本地客户端映射表 channelID <--> Channel
    private static final Map<String, Channel> CHANNEL_MAP = new ConcurrentHashMap<>();
    //本地用户与客户端连接的双向注册表 uid <--> channelID
    private static final BiMap<String, String> LOCAL_BINDING_MAP = HashBiMap.create();
    //全局用户与客户端连接的双向注册表（监听zk事件并随之更新）
    private static final BiMap<String, String> GLOBAL_BINDING_MAP = HashBiMap.create();

    public void bind(String uid, Channel channel) {
        //1.本地注册
        //2.zk注册
        //todo
    }

    public void unbind(Channel channel) {
        //1.本地卸载
        //2.zk卸载
        //todo
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
