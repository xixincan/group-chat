package com.xxc.service;

import com.github.pagehelper.PageHelper;
import com.xxc.common.util.MyPageUtil;
import com.xxc.dao.mapper.UserMapper;
import com.xxc.dao.model.User;
import com.xxc.entity.result.MyPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @Author: xixincan
 * @Date: 2019/5/20
 * @Version 1.0
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public List<User> get(String name) {
        Example example = new Example(User.class);
        example.createCriteria().andEqualTo("name", name);
        return this.userMapper.selectByExample(example);
    }

    public List<User> getAll() {
        return this.userMapper.selectAll();
    }

    public MyPage<User> page(int page, int size) {
        PageHelper.startPage(page, size);
        List<User> userList = this.getAll();
        return MyPageUtil.genPage(userList, userList);
    }

}
