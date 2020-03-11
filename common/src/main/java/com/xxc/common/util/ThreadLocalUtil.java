package com.xxc.common.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.log.StaticLog;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author xixincan
 * 2020-03-11
 * @version 1.0.0
 */
public class ThreadLocalUtil {

    private static final ThreadLocal<Map<String, Object>> RESOURCES = new ThreadLocal<>();

    public static void bind(String key, Object value) {
        Map<String, Object> map = RESOURCES.get();
        if (null == map) {
            map = new HashMap<>();
            RESOURCES.set(map);
        }
        map.put(key, value);
    }

    public static boolean hasResource(String key) {
        return (get(key) != null);
    }

    public static <T> T get(String key) {
        Map<String, Object> map = RESOURCES.get();
        if (null == map) {
            return null;
        }
        return (T) map.get(key);
    }

    public static <T> T get(String key, Function<String, T> function) {
        T value = get(key);
        if (value == null) {
            value = function.apply(key);
            if (value != null) {
                bind(key, value);
            }
        }
        return value;
    }

    public static void unBindAll() {
        Map<String, Object> map = RESOURCES.get();
        if (CollectionUtil.isNotEmpty(map)) {
            map.clear();
            map = null;
            RESOURCES.remove();
        }
    }

    public static Object unBind(String key) {
        Map<String, Object> map = RESOURCES.get();
        if (map == null) {
            return null;
        }
        Object value = map.remove(key);

        if (map.isEmpty()) {
            RESOURCES.set(null);
        }
        if (value != null) {
            StaticLog.debug("Removed value [{}] for key [{}] from thread [{}]", value, key, Thread.currentThread().getName());
        }
        return value;
    }

    public static void unBind(String... keys) {
        if (ArrayUtil.isEmpty(keys)) {
            return;
        }
        Map<String, Object> map = RESOURCES.get();
        if (map == null) {
            return;
        }
        for (String key : keys) {
            map.remove(key);
        }
        if (map.isEmpty()) {
            RESOURCES.set(null);
        }
    }


}
