package com.example;

/**
 * 雪花算法（Snowflake）分布式唯一ID生成器
 *
 * ID结构（64位long型）:
 * 0 | 00000000000000000000000000000000000000000 | 00000 | 00000 | 000000000000
 * 1位符号位 |          41位时间戳              |5位DC |5位W  |  12位序列号
 *
 * 特性:
 * - 全局唯一：分布式环境下ID不重复
 * - 趋势递增：按时间趋势递增，利于索引
 * - 高性能：单机每秒可生成400万+个ID
 * - 线程安全：synchronized保证
 * - 时钟回拨保护：检测并拒绝时钟回拨
 */
public class SnowflakeIdUtils {

    private static final long TWEPOCH = 1577808000000L;

    private static final long WORKER_ID_BITS = 5L;
    private static final long DATACENTER_ID_BITS = 5L;
    private static final long SEQUENCE_BITS = 12L;

    private static final long MAX_WORKER_ID = -1L ^ (-1L << WORKER_ID_BITS);
    private static final long MAX_DATACENTER_ID = -1L ^ (-1L << DATACENTER_ID_BITS);
    private static final long SEQUENCE_MASK = -1L ^ (-1L << SEQUENCE_BITS);

    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;

    private final long workerId;
    private final long datacenterId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    private static volatile SnowflakeIdUtils instance;

    private static final long DEFAULT_WORKER_ID = 0L;
    private static final long DEFAULT_DATACENTER_ID = 0L;

    private SnowflakeIdUtils(long workerId, long datacenterId) {
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException(
                    String.format("workerId 范围: [0, %d]", MAX_WORKER_ID));
        }
        if (datacenterId > MAX_DATACENTER_ID || datacenterId < 0) {
            throw new IllegalArgumentException(
                    String.format("datacenterId 范围: [0, %d]", MAX_DATACENTER_ID));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    public static SnowflakeIdUtils getInstance() {
        if (instance == null) {
            synchronized (SnowflakeIdUtils.class) {
                if (instance == null) {
                    instance = new SnowflakeIdUtils(DEFAULT_WORKER_ID, DEFAULT_DATACENTER_ID);
                }
            }
        }
        return instance;
    }

    public static void init(long workerId, long datacenterId) {
        if (instance != null) {
            throw new IllegalStateException("SnowflakeIdUtils 已初始化，不可重复初始化");
        }
        synchronized (SnowflakeIdUtils.class) {
            if (instance != null) {
                throw new IllegalStateException("SnowflakeIdUtils 已初始化，不可重复初始化");
            }
            instance = new SnowflakeIdUtils(workerId, datacenterId);
        }
    }

    public static long nextId() {
        return getInstance().generateId();
    }

    public synchronized long generateId() {
        long timestamp = System.currentTimeMillis();

        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                    String.format("时钟回退异常，拒绝生成ID，回退了 %d 毫秒", lastTimestamp - timestamp));
        }

        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) {
                timestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - TWEPOCH) << TIMESTAMP_LEFT_SHIFT)
                | (datacenterId << DATACENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }

    public static ParsedId parseId(long id) {
        long timestamp = ((id >> TIMESTAMP_LEFT_SHIFT) & ~(-1L << 41L)) + TWEPOCH;
        long dcId = (id >> DATACENTER_ID_SHIFT) & ~(-1L << DATACENTER_ID_BITS);
        long wId = (id >> WORKER_ID_SHIFT) & ~(-1L << WORKER_ID_BITS);
        long seq = id & SEQUENCE_MASK;
        return new ParsedId(id, timestamp, dcId, wId, seq);
    }

    private long waitNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

    public long getWorkerId() {
        return workerId;
    }

    public long getDatacenterId() {
        return datacenterId;
    }

    public static class ParsedId {
        private final long id;
        private final long timestamp;
        private final long datacenterId;
        private final long workerId;
        private final long sequence;

        public ParsedId(long id, long timestamp, long datacenterId, long workerId, long sequence) {
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

        @Override
        public String toString() {
            return "ParsedId{" +
                    "id=" + id +
                    ", timestamp=" + timestamp +
                    ", datacenterId=" + datacenterId +
                    ", workerId=" + workerId +
                    ", sequence=" + sequence +
                    '}';
        }
    }

    public static void main(String[] args) {
        System.out.println("===== 雪花算法生成唯一ID演示 =====");
        System.out.println();

        for (int i = 0; i < 10; i++) {
            long id = SnowflakeIdUtils.nextId();
            ParsedId parsed = SnowflakeIdUtils.parseId(id);
            System.out.printf("ID: %d | 时间戳: %d | 数据中心: %d | 机器: %d | 序列号: %d%n",
                    parsed.getId(), parsed.getTimestamp(), parsed.getDatacenterId(),
                    parsed.getWorkerId(), parsed.getSequence());
        }
    }
}