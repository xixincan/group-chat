package com.xxc.common.cache;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @Author: xixincan
 * @Date: 2019-05-20
 * @Version 1.0
 */
@Service
public class RedisService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 这里不能使用{@Autowired}注解
     */
    @Resource
    private RedisTemplate<String, Integer> redisTemplate;

    public String getString(String key) {
        return this.stringRedisTemplate.opsForValue().get(key);
    }

    public void setString(String key, String value) {
        this.stringRedisTemplate.opsForValue().set(key, value);
    }

    public Integer getInteger(String key) {
        return this.redisTemplate.opsForValue().get(key);
    }

    public void setInteger(String key, Integer value) {
        this.redisTemplate.opsForValue().set(key, value);
    }

    public void setInteger(String key, Integer value, long timeout) {
        this.redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    public void remove(String key) {
        this.redisTemplate.delete(key);
    }

}
