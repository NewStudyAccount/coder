package com.admin.service.impl;

import cn.hutool.core.util.StrUtil;
import com.admin.common.result.PageParam;
import com.admin.common.result.PageResult;
import com.admin.entity.Config;
import com.admin.mapper.ConfigMapper;
import com.admin.service.ConfigService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConfigServiceImpl implements ConfigService {

    private final ConfigMapper configMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String CONFIG_CACHE_KEY = "sys:config:";

    @Override
    public PageResult<Config> selectConfigList(PageParam pageParam, String configName, String configKey, Integer configType) {
        LambdaQueryWrapper<Config> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(configName), Config::getConfigName, configName)
               .like(StrUtil.isNotBlank(configKey), Config::getConfigKey, configKey)
               .eq(configType != null, Config::getConfigType, configType);

        Page<Config> page = configMapper.selectPage(new Page<>(pageParam.getPageNum(), pageParam.getPageSize()), wrapper);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @Override
    public Config selectConfigById(Long id) {
        return configMapper.selectById(id);
    }

    @Override
    public String selectConfigByKey(String configKey) {
        String cacheKey = CONFIG_CACHE_KEY + configKey;
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached.toString();
        }

        Config config = configMapper.selectOne(new LambdaQueryWrapper<Config>().eq(Config::getConfigKey, configKey));
        if (config != null) {
            redisTemplate.opsForValue().set(cacheKey, config.getConfigValue());
            return config.getConfigValue();
        }
        return null;
    }

    @Override
    public void createConfig(Config config) {
        config.setCreateBy("system");
        configMapper.insert(config);
        redisTemplate.opsForValue().set(CONFIG_CACHE_KEY + config.getConfigKey(), config.getConfigValue());
    }

    @Override
    public void updateConfig(Config config) {
        config.setUpdateBy("system");
        configMapper.updateById(config);
        redisTemplate.opsForValue().set(CONFIG_CACHE_KEY + config.getConfigKey(), config.getConfigValue());
    }

    @Override
    public void deleteConfig(Long id) {
        Config config = configMapper.selectById(id);
        if (config != null) {
            redisTemplate.delete(CONFIG_CACHE_KEY + config.getConfigKey());
        }
        configMapper.deleteById(id);
    }
}
