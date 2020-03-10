package com.xxc.dao.mapper;

import com.xxc.dao.base.CustomMapper;
import com.xxc.dao.model.Config;

import java.util.List;

public interface ConfigMapper extends CustomMapper<Config> {

    int updateBatch(List<Config> configList);

}