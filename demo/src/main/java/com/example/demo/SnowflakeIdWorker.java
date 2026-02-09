package com.example.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Twitter 雪花算法ID生成器
 * 
 * ID结构: 64位long型数字
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000
 * 1位符号位 | 41位时间戳 | 5位数据中心ID | 5位机器ID | 12位序列号
 * 
 * 特性:
 * - 全局唯一
 * - 趋势递增
 * - 高性能(单机400万+/秒)
 * - 无依赖
 * - 线程安全
 */
@Component
public class SnowflakeIdWorker {
    
    // ==============================常量定义==============================
    
    /** 起始时间戳 (2015-01-01 00:00:00) */
    private final long twepoch = 1420041600000L;
    
    /** 机器ID所占的位数 */
    private final long workerIdBits = 5L;
    
    /** 数据中心ID所占的位数 */
    private final long datacenterIdBits = 5L;
    
    /** 支持的最大机器ID，结果是31 (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数) */
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);
    
    /** 支持的最大数据中心ID，结果是31 */
    private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);
    
    /** 序列在ID中占的位数 */
    private final long sequenceBits = 12L;
    
    /** 机器ID向左移12位 */
    private final long workerIdShift = sequenceBits;
    
    /** 数据中心ID向左移17位(12+5) */
    private final long datacenterIdShift = sequenceBits + workerIdBits;
    
    /** 时间戳向左移22位(5+5+12) */
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
    
    /** 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095) */
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);
    
    // ==============================字段定义==============================
    
    /** 工作机器ID(0~31) */
    private long workerId;
    
    /** 数据中心ID(0~31) */
    private long datacenterId;
    
    /** 毫秒内序列(0~4095) */
    private long sequence = 0L;
    
    /** 上次生成ID的时间戳 */
    private long lastTimestamp = -1L;
    
    // ==============================构造函数==============================
    
    /**
     * 构造函数
     * @param workerId 工作ID (0~31)
     * @param datacenterId 数据中心ID (0~31)
     */
    public SnowflakeIdWorker(@Value("${snowflake.worker-id:0}") long workerId,
                             @Value("${snowflake.datacenter-id:0}") long datacenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(
                String.format("worker Id 不能大于 %d 或小于 0", maxWorkerId));
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(
                String.format("datacenter Id 不能大于 %d 或小于 0", maxDatacenterId));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }
    
    // ==============================核心方法==============================
    
    /**
     * 获得下一个ID (线程安全)
     * @return SnowflakeId
     */
    public synchronized long nextId() {
        long timestamp = timeGen();
        
        // 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过，这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                String.format("时钟回退异常，拒绝生成ID。回退了 %d 毫秒", lastTimestamp - timestamp));
        }
        
        // 如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            // 毫秒内序列溢出
            if (sequence == 0) {
                // 阻塞到下一个毫秒，获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        }
        // 时间戳改变，毫秒内序列重置
        else {
            sequence = 0L;
        }
        
        // 上次生成ID的时间戳
        lastTimestamp = timestamp;
        
        // 移位并通过或运算拼到一起组成64位的ID
        return ((timestamp - twepoch) << timestampLeftShift)
                | (datacenterId << datacenterIdShift)
                | (workerId << workerIdShift)
                | sequence;
    }
    
    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     * @param lastTimestamp 上次生成ID的时间戳
     * @return 当前时间戳
     */
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }
    
    /**
     * 返回以毫秒为单位的当前时间
     * @return 当前时间(毫秒)
     */
    protected long timeGen() {
        return System.currentTimeMillis();
    }
    
    // ==============================辅助方法==============================
    
    /**
     * 解析雪花ID
     * @param id 雪花ID
     * @return ID信息对象
     */
    public SnowflakeIdInfo parseId(long id) {
        long timestamp = ((id >> timestampLeftShift) & ~(-1L << 41L)) + twepoch;
        long datacenterId = (id >> datacenterIdShift) & ~(-1L << datacenterIdBits);
        long workerId = (id >> workerIdShift) & ~(-1L << workerIdBits);
        long sequence = id & sequenceMask;
        
        return new SnowflakeIdInfo(id, timestamp, datacenterId, workerId, sequence);
    }
    
    /**
     * 获取当前配置信息
     */
    public SnowflakeConfig getConfig() {
        return new SnowflakeConfig(
            workerId, 
            datacenterId, 
            maxWorkerId, 
            maxDatacenterId, 
            sequenceMask
        );
    }
    
    // ==============================内部类==============================
    
    /**
     * 雪花ID信息类
     */
    public static class SnowflakeIdInfo {
        private final long id;
        private final long timestamp;
        private final long datacenterId;
        private final long workerId;
        private final long sequence;
        
        public SnowflakeIdInfo(long id, long timestamp, long datacenterId, 
                               long workerId, long sequence) {
            this.id = id;
            this.timestamp = timestamp;
            this.datacenterId = datacenterId;
            this.workerId = workerId;
            this.sequence = sequence;
        }
        
        public long getId() { return id; }
        public long getTimestamp() { return timestamp; }
        public long getDatacenterId() { return datacenterId; }
        public long getWorkerId() { return workerId; }
        public long getSequence() { return sequence; }
    }
    
    /**
     * 雪花算法配置信息类
     */
    public static class SnowflakeConfig {
        private final long workerId;
        private final long datacenterId;
        private final long maxWorkerId;
        private final long maxDatacenterId;
        private final long maxSequence;
        
        public SnowflakeConfig(long workerId, long datacenterId, 
                              long maxWorkerId, long maxDatacenterId, long maxSequence) {
            this.workerId = workerId;
            this.datacenterId = datacenterId;
            this.maxWorkerId = maxWorkerId;
            this.maxDatacenterId = maxDatacenterId;
            this.maxSequence = maxSequence;
        }
        
        public long getWorkerId() { return workerId; }
        public long getDatacenterId() { return datacenterId; }
        public long getMaxWorkerId() { return maxWorkerId; }
        public long getMaxDatacenterId() { return maxDatacenterId; }
        public long getMaxSequence() { return maxSequence; }
    }
}
