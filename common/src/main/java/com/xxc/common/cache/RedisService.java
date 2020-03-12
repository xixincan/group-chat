package com.xxc.common.cache;

import cn.hutool.log.StaticLog;
import com.xxc.common.util.SerializeUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @Author: xixincan
 * @Date: 2019-05-20
 * @Version 1.0
 */
@Service
public class RedisService {

    /**
     * 这里不能使用 Autowired 注解
     */
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @PostConstruct
    public void init() {
        //设置Redis中key的序列化方式为StringRedisSerializer；
        //默认键和值都使用JdkSerializationRedisSerializer来进行序列化
        RedisSerializer stringSerializer = new StringRedisSerializer();
        this.redisTemplate.setKeySerializer(stringSerializer);
        this.redisTemplate.setHashKeySerializer(stringSerializer);
        StaticLog.info("RedisService初始化完毕.");
    }

    public String getString(String key) {
        Object value = this.redisTemplate.opsForValue().get(key);
        if (null == value) {
            return null;
        }
        return String.valueOf(value);
    }

    public void setString(String key, String value) {
        this.redisTemplate.opsForValue().set(key, value);
    }

    public void setString(String key, String value, long timeout) {
        this.redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    public Integer getInteger(String key) {
        Object value = this.redisTemplate.opsForValue().get(key);
        if (null == value) {
            return null;
        }
        return Integer.valueOf(String.valueOf(value));
    }

    public void setInteger(String key, Integer value) {
        this.setString(key, String.valueOf(value));
    }

    public void setInteger(String key, Integer value, long timeout) {
        this.setString(key, String.valueOf(value), timeout);
    }

    public void remove(String key) {
        this.redisTemplate.delete(key);
    }

    public void serializeSave(String key, Object value) {
        byte[] bytes = SerializeUtil.serialize(value);
        this.redisTemplate.opsForValue().set(key, value);
    }

    public void serializeSave(String key, Object value, long timeout) {
        if (value == null) {
            return;
        }
        byte[] bytes = SerializeUtil.serialize(value);
        this.redisTemplate.opsForValue().set(key, bytes, timeout, TimeUnit.SECONDS);
    }

    public <T> T serializeGet(String key, Class<T> tClass) {
        Object value = this.redisTemplate.opsForValue().get(key);
        if (null == value) {
            return null;
        }
        return SerializeUtil.deserialize((byte[]) value, tClass);
    }

    public boolean exist(String key) {
        return Boolean.TRUE.equals(this.redisTemplate.hasKey(key));
    }
}
