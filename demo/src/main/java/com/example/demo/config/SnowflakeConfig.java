package com.example.demo.config;

import com.example.demo.SnowflakeIdWorker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 雪花算法配置类
 * 将SnowflakeIdWorker配置为Spring Bean，方便在项目中注入使用
 */
@Configuration
public class SnowflakeConfig {

    /**
     * 工作机器ID，从配置文件读取
     * 取值范围：0-31
     */
    @Value("${snowflake.worker-id:0}")
    private long workerId;

    /**
     * 数据中心ID，从配置文件读取
     * 取值范围：0-31
     */
    @Value("${snowflake.datacenter-id:0}")
    private long datacenterId;

    /**
     * 创建雪花算法ID生成器Bean
     * 
     * @return SnowflakeIdWorker实例
     */
    @Bean
    public SnowflakeIdWorker snowflakeIdWorker() {
        return new SnowflakeIdWorker(workerId, datacenterId);
    }
}
