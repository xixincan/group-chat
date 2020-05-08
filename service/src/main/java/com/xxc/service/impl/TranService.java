package com.xxc.service.impl;

import com.xxc.dao.mapper.UserMapper;
import com.xxc.service.ITranService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author xixincan
 * 2020-05-08
 * @version 1.0.0
 */
@Service
public class TranService implements ITranService {

    @Resource
    private UserMapper userMapper;

    @Override
    public void transGet(String uid) {

    }
}
