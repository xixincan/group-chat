package com.xxc.dao.base;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * Created by xi yang.
 * 2018/9/11
 */
public interface CustomMapper<T> extends MySqlMapper<T>, Mapper<T> {
}
