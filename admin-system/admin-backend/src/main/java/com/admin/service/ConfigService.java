package com.admin.service;

import com.admin.common.result.PageParam;
import com.admin.common.result.PageResult;
import com.admin.entity.Config;

public interface ConfigService {

    PageResult<Config> selectConfigList(PageParam pageParam, String configName, String configKey, Integer configType);

    Config selectConfigById(Long id);

    String selectConfigByKey(String configKey);

    void createConfig(Config config);

    void updateConfig(Config config);

    void deleteConfig(Long id);
}
