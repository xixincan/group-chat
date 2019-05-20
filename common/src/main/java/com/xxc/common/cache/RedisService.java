package com.xxc.common.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @Author: xixincan
 * @Date: 2019-05-20
 * @Version 1.0
 */
@Service
public class RedisService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    public String getString(String key) {
        return this.stringRedisTemplate.opsForValue().get(key);
    }

}
