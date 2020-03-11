package com.xxc.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.StaticLog;
import com.google.common.collect.Maps;
import com.xxc.dao.mapper.ConfigMapper;
import com.xxc.dao.model.Config;
import com.xxc.entity.exp.AccessException;
import com.xxc.service.IConfigService;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author xixincan
 * 2020-03-10
 * @version 1.0.0
 */
@Service
public class ConfigService implements IConfigService {

    private static Map<String, Config> map = null;

    private static List<Config> list = null;

    @Resource
    private ConfigMapper configMapper;

    @Override
    public void reload() {
        //todo 修改为Redis缓存
        List<Config> configs = this.configMapper.selectAll();
        Map<String, Config> temp = Maps.newHashMap();
        if (CollectionUtil.isNotEmpty(configs)) {
            configs.forEach(config -> {
                StaticLog.debug(JSONUtil.toJsonStr(config));
                temp.put(config.getKeyword(), config);
            });
        }
        list = configs;
        map = temp;
    }

    @Override
    public List<Config> getAll() {
        if (null == list) {
            this.reload();
        }
        return list;
    }

    @Override
    public Config getConfig(String key) {
        if (null == map) {
            this.reload();
        }
        return map.get(key);
    }

    @Override
    public String getValue(String key) {
        Config config = this.getConfig(key);
        if (null == config) {
            return null;
        }
        return config.getValue();
    }

    @Override
    public Boolean getBooleanValue(String key) {
        Config config = this.getConfig(key);
        if (null == config) {
            return null;
        }
        return Boolean.valueOf(config.getValue());
    }

    @Override
    public Integer getIntegerValue(String key) {
        Config config = this.getConfig(key);
        if (null == config) {
            return null;
        }
        return Integer.valueOf(config.getValue());
    }

    @Override
    public Long getLongValue(String key) {
        Config config = this.getConfig(key);
        if (null == config) {
            return null;
        }
        return Long.valueOf(config.getValue());
    }

    @Override
    public Double getDoubleValue(String key) {
        Config config = this.getConfig(key);
        if (null == config) {
            return null;
        }
        return Double.valueOf(config.getValue());
    }

    @Override
    public BigDecimal getBigDecimalValue(String key) {
        Config config = this.getConfig(key);
        if (null == config) {
            return null;
        }
        return new BigDecimal(config.getValue());
    }

    @Override
    public void insert(Config config) {
        if (null == config) {
            throw new AccessException("配置为空");
        }
        if (this.keyExist(config.getKeyword())) {
            StaticLog.error("键值不能重复:{}", config.getKeyword());
            throw new AccessException("键值不能重复");
        }
        this.configMapper.insert(config);
    }

    @Override
    public void insertBatch(List<Config> configList) {
        this.configMapper.insertList(configList);
    }

    @Override
    public int update(Config config) {
        return this.configMapper.updateByPrimaryKeySelective(config);
    }

    @Override
    public int updateBatch(List<Config> configList) {
        return this.configMapper.updateBatch(configList);
    }

    /**
     * 该keyword是否存在
     *
     * @return bool
     */
    private boolean keyExist(String keyword) {
        Example example = new Example(Config.class);
        example.createCriteria().andEqualTo("keyword", keyword);
        return this.configMapper.selectCountByExample(example) > 0;
    }
}
