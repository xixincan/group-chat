package com.xxc.service;

import com.xxc.dao.model.Config;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author xixincan
 * 2020-03-10
 * @version 1.0.0
 */
public interface IConfigService {

    void reload();

    List<Config> getAll();

    Config getConfig(String key);

    String getValue(String key);

    Boolean getBooleanValue(String key);

    Integer getIntegerValue(String key);

    Long getLongValue(String key);

    Double getDoubleValue(String key);

    BigDecimal getBigDecimalValue(String key);

    void insert(Config config);

    void insertBatch(List<Config> configList);

    int update(Config config);

    int updateBatch(List<Config> configList);

}
