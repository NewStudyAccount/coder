package com.example.demo;

/**
 * 唯一ID生成器工具类（基于雪花算法 Snowflake）
 * 提供全局唯一ID生成，适用于分布式系统
 */
public class UniqueIdGenerator {
    // 起始时间戳（2020-01-01）
    private final long twepoch = 1577808000000L;
    // 机器ID所占位数
    private final long workerIdBits = 5L;
    // 数据中心ID所占位数
    private final long datacenterIdBits = 5L;
    // 序列号所占位数
    private final long sequenceBits = 12L;

    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);
    private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);

    private final long workerIdShift = sequenceBits;
    private final long datacenterIdShift = sequenceBits + workerIdBits;
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    private long workerId;
    private long datacenterId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    // 单例实例
    private static volatile UniqueIdGenerator instance;

    // 默认参数，可根据实际情况调整
    private static final long DEFAULT_WORKER_ID = 1L;
    private static final long DEFAULT_DATACENTER_ID = 1L;

    private UniqueIdGenerator(long workerId, long datacenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    /**
     * 获取单例实例
     */
    public static UniqueIdGenerator getInstance() {
        if (instance == null) {
            synchronized (UniqueIdGenerator.class) {
                if (instance == null) {
                    instance = new UniqueIdGenerator(DEFAULT_WORKER_ID, DEFAULT_DATACENTER_ID);
                }
            }
        }
        return instance;
    }

    /**
     * 生成唯一ID（静态工具方法）
     */
    public static long nextId() {
        return getInstance().generateId();
    }

    /**
     * 生成唯一ID（实例方法）
     */
    public synchronized long generateId() {
        long timestamp = timeGen();

        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                String.format("Clock moved backwards. Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - twepoch) << timestampLeftShift)
                | (datacenterId << datacenterIdShift)
                | (workerId << workerIdShift)
                | sequence;
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }

    public static void main(String[] args) {
        System.out.println("唯一ID生成器生成10个ID：");
        for (int i = 0; i < 10; i++) {
            System.out.println(UniqueIdGenerator.nextId());
        }
    }
}