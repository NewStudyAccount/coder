package com.example.demo;

import org.springframework.stereotype.Component;

/**
 * 雪花算法(Snowflake)分布式ID生成器
 * 
 * <p>雪花算法生成的ID是一个64位的long型数字,其结构如下:
 * <pre>
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000
 * |   |                                               |   |       |       |
 * |   |                                               |   |       |       +------ 序列号(12位)
 * |   |                                               |   |       +-------------- 机器ID(5位)
 * |   |                                               |   +-------------------- 数据中心ID(5位)
 * |   |                                               +----------------------- 工作机器ID(10位)
 * |   +------------------------------------------------------------------- 时间戳(41位)
 * +---------------------------------------------------------------------- 符号位(1位,始终为0)
 * </pre>
 * 
 * <p>ID组成部分:
 * <ul>
 *   <li>1位符号位: 始终为0,表示正数</li>
 *   <li>41位时间戳: 毫秒级时间戳,可以使用69年</li>
 *   <li>10位工作机器ID: 包含5位数据中心ID和5位机器ID,最多支持1024个节点</li>
 *   <li>12位序列号: 同一毫秒内的序列号,最多支持4096个ID</li>
 * </ul>
 * 
 * <p>特点:
 * <ul>
 *   <li>全局唯一: 在分布式环境下保证ID的全局唯一性</li>
 *   <li>趋势递增: 生成的ID大致按照时间递增</li>
 *   <li>高性能: 单机每秒可生成400万个ID</li>
 *   <li>无依赖: 不依赖第三方系统,通过算法生成ID</li>
 * </ul>
 * 
 * @author AI Commander
 * @since 2026-02-02
 */
@Component
public class SnowflakeIdWorker {

    // ==============================常量定义==============================
    
    /** 起始时间戳(2020-01-01 00:00:00) */
    private static final long EPOCH = 1577808000000L;
    
    /** 机器ID所占的位数 */
    private static final long WORKER_ID_BITS = 5L;
    
    /** 数据中心ID所占的位数 */
    private static final long DATACENTER_ID_BITS = 5L;
    
    /** 序列号所占的位数 */
    private static final long SEQUENCE_BITS = 12L;
    
    /** 机器ID最大值: 31 (这个移位算法可以很快计算出最大值) */
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    
    /** 数据中心ID最大值: 31 */
    private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);
    
    /** 序列号最大值: 4095 */
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);
    
    /** 机器ID向左移12位 */
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    
    /** 数据中心ID向左移17位(12+5) */
    private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    
    /** 时间戳向左移22位(12+5+5) */
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;
    
    // ==============================实例变量==============================
    
    /** 工作机器ID(0~31) */
    private final long workerId;
    
    /** 数据中心ID(0~31) */
    private final long datacenterId;
    
    /** 毫秒内序列(0~4095) */
    private long sequence = 0L;
    
    /** 上次生成ID的时间戳 */
    private long lastTimestamp = -1L;
    
    // ==============================构造函数==============================
    
    /**
     * 构造函数
     * 
     * @param workerId     工作机器ID (0~31)
     * @param datacenterId 数据中心ID (0~31)
     */
    public SnowflakeIdWorker(long workerId, long datacenterId) {
        // 检查workerId合法性
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException(
                String.format("Worker ID 不能大于 %d 或小于 0", MAX_WORKER_ID)
            );
        }
        // 检查datacenterId合法性
        if (datacenterId > MAX_DATACENTER_ID || datacenterId < 0) {
            throw new IllegalArgumentException(
                String.format("Datacenter ID 不能大于 %d 或小于 0", MAX_DATACENTER_ID)
            );
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }
    
    /**
     * 默认构造函数
     * 使用默认的workerId=0, datacenterId=0
     */
    public SnowflakeIdWorker() {
        this(0L, 0L);
    }
    
    // ==============================核心方法==============================
    
    /**
     * 获得下一个ID (线程安全)
     * 
     * @return 雪花ID
     */
    public synchronized long nextId() {
        long timestamp = timeGen();
        
        // 如果当前时间小于上一次ID生成的时间戳,说明系统时钟回退过,抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                String.format("时钟回退异常。拒绝生成ID %d 毫秒", lastTimestamp - timestamp)
            );
        }
        
        // 如果是同一毫秒内生成的,则进行序列号递增
        if (timestamp == lastTimestamp) {
            // 通过位与运算保证序列号在0~4095之间循环
            sequence = (sequence + 1) & MAX_SEQUENCE;
            
            // 序列号溢出,阻塞到下一毫秒
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            // 时间戳改变,序列号重置
            sequence = 0L;
        }
        
        // 更新上次生成ID的时间戳
        lastTimestamp = timestamp;
        
        // 移位并通过或运算拼接成64位的ID
        return ((timestamp - EPOCH) << TIMESTAMP_SHIFT)    // 时间戳部分
                | (datacenterId << DATACENTER_ID_SHIFT)     // 数据中心部分
                | (workerId << WORKER_ID_SHIFT)             // 机器ID部分
                | sequence;                                  // 序列号部分
    }
    
    /**
     * 阻塞到下一毫秒,直到获得新的时间戳
     * 
     * @param lastTimestamp 上次生成ID的时间戳
     * @return 当前时间戳
     */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }
    
    /**
     * 返回以毫秒为单位的当前时间
     * 
     * @return 当前时间(毫秒)
     */
    private long timeGen() {
        return System.currentTimeMillis();
    }
    
    // ==============================解析方法==============================
    
    /**
     * 解析雪花ID
     * 
     * @param id 雪花ID
     * @return ID信息对象
     */
    public static SnowflakeIdInfo parseId(long id) {
        long timestamp = (id >> TIMESTAMP_SHIFT) + EPOCH;
        long datacenterId = (id >> DATACENTER_ID_SHIFT) & MAX_DATACENTER_ID;
        long workerId = (id >> WORKER_ID_SHIFT) & MAX_WORKER_ID;
        long sequence = id & MAX_SEQUENCE;
        
        return new SnowflakeIdInfo(id, timestamp, datacenterId, workerId, sequence);
    }
    
    // ==============================内部类==============================
    
    /**
     * 雪花ID信息
     */
    public static class SnowflakeIdInfo {
        /** 原始ID */
        private final long id;
        /** 时间戳 */
        private final long timestamp;
        /** 数据中心ID */
        private final long datacenterId;
        /** 工作机器ID */
        private final long workerId;
        /** 序列号 */
        private final long sequence;
        
        public SnowflakeIdInfo(long id, long timestamp, long datacenterId, long workerId, long sequence) {
            this.id = id;
            this.timestamp = timestamp;
            this.datacenterId = datacenterId;
            this.workerId = workerId;
            this.sequence = sequence;
        }
        
        public long getId() {
            return id;
        }
        
        public long getTimestamp() {
            return timestamp;
        }
        
        public long getDatacenterId() {
            return datacenterId;
        }
        
        public long getWorkerId() {
            return workerId;
        }
        
        public long getSequence() {
            return sequence;
        }
        
        @Override
        public String toString() {
            return String.format(
                "SnowflakeIdInfo{id=%d, timestamp=%d(%s), datacenterId=%d, workerId=%d, sequence=%d}",
                id, timestamp, new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new java.util.Date(timestamp)),
                datacenterId, workerId, sequence
            );
        }
    }
    
    // ==============================测试方法==============================
    
    /**
     * 测试方法
     */
    public static void main(String[] args) {
        // 创建ID生成器(workerId=1, datacenterId=1)
        SnowflakeIdWorker idWorker = new SnowflakeIdWorker(1, 1);
        
        System.out.println("=== 雪花算法ID生成演示 ===\n");
        
        // 生成10个ID
        System.out.println("生成10个连续ID:");
        for (int i = 0; i < 10; i++) {
            long id = idWorker.nextId();
            System.out.printf("ID %d: %d%n", i + 1, id);
        }
        
        System.out.println("\n=== ID解析演示 ===\n");
        
        // 解析一个ID
        long sampleId = idWorker.nextId();
        SnowflakeIdInfo info = SnowflakeIdWorker.parseId(sampleId);
        System.out.println("生成的ID: " + sampleId);
        System.out.println("解析结果: " + info);
        
        System.out.println("\n=== 性能测试 ===\n");
        
        // 性能测试: 生成100万个ID
        long startTime = System.currentTimeMillis();
        int count = 1_000_000;
        for (int i = 0; i < count; i++) {
            idWorker.nextId();
        }
        long endTime = System.currentTimeMillis();
        
        System.out.printf("生成 %,d 个ID耗时: %d 毫秒%n", count, endTime - startTime);
        System.out.printf("平均每秒生成: %,d 个ID%n", count * 1000 / (endTime - startTime));
        
        System.out.println("\n=== 并发测试 ===\n");
        
        // 并发测试
        testConcurrency(idWorker);
    }
    
    /**
     * 并发测试
     */
    private static void testConcurrency(SnowflakeIdWorker idWorker) {
        int threadCount = 10;
        int idCountPerThread = 10000;
        java.util.Set<Long> idSet = java.util.Collections.synchronizedSet(new java.util.HashSet<>());
        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(threadCount);
        
        long startTime = System.currentTimeMillis();
        
        // 创建多个线程并发生成ID
        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                try {
                    for (int j = 0; j < idCountPerThread; j++) {
                        long id = idWorker.nextId();
                        idSet.add(id);
                    }
                } finally {
                    latch.countDown();
                }
            }).start();
        }
        
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        long endTime = System.currentTimeMillis();
        int totalCount = threadCount * idCountPerThread;
        
        System.out.printf("并发测试: %d 个线程,每个线程生成 %,d 个ID%n", threadCount, idCountPerThread);
        System.out.printf("总共生成: %,d 个ID%n", totalCount);
        System.out.printf("去重后数量: %,d 个ID%n", idSet.size());
        System.out.printf("是否有重复: %s%n", idSet.size() == totalCount ? "否" : "是");
        System.out.printf("总耗时: %d 毫秒%n", endTime - startTime);
        System.out.printf("平均每秒生成: %,d 个ID%n", totalCount * 1000 / (endTime - startTime));
    }
}
