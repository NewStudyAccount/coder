package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;

/**
 * 雪花算法 REST API 控制器
 * 提供ID生成、解析、性能测试等接口
 * 
 * @author AI Commander
 * @version 2.0
 */
@RestController
@RequestMapping("/api/snowflake")
@CrossOrigin(origins = "*")
public class SnowflakeController {
    
    // 常量定义
    private static final int MAX_BATCH_COUNT = 1000;
    private static final int DEFAULT_BATCH_COUNT = 10;
    private static final int MAX_PERFORMANCE_COUNT = 100000;
    private static final int DEFAULT_PERFORMANCE_COUNT = 10000;
    private static final int MAX_THREADS = 50;
    private static final int DEFAULT_THREADS = 10;
    private static final int MAX_COUNT_PER_THREAD = 10000;
    private static final int DEFAULT_COUNT_PER_THREAD = 1000;
    
    // 使用线程安全的DateTimeFormatter替代SimpleDateFormat
    private static final DateTimeFormatter DATE_TIME_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;
    
    /**
     * 生成单个ID
     * 
     * @return 包含生成ID的响应对象
     */
    @GetMapping("/generate")
    public ResponseEntity<ApiResponse<IdGenerateResult>> generate() {
        try {
            long id = snowflakeIdWorker.nextId();
            
            IdGenerateResult data = new IdGenerateResult();
            data.setId(id);
            data.setIdStr(String.valueOf(id));
            data.setTimestamp(System.currentTimeMillis());
            
            return ResponseEntity.ok(ApiResponse.success(data));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("生成ID失败: " + e.getMessage()));
        }
    }
    
    /**
     * 批量生成ID
     * 
     * @param count 生成数量，默认10个，最多1000个
     * @return 包含生成ID列表的响应对象
     */
    @GetMapping("/batch")
    public ResponseEntity<ApiResponse<BatchGenerateResult>> batch(
            @RequestParam(defaultValue = "" + DEFAULT_BATCH_COUNT) int count) {
        
        // 参数验证
        if (count <= 0) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("生成数量必须大于0"));
        }
        
        // 限制最大数量
        int validCount = Math.min(count, MAX_BATCH_COUNT);
        
        try {
            List<Long> ids = new ArrayList<>(validCount);
            for (int i = 0; i < validCount; i++) {
                ids.add(snowflakeIdWorker.nextId());
            }
            
            BatchGenerateResult data = new BatchGenerateResult();
            data.setCount(validCount);
            data.setIds(ids);
            
            return ResponseEntity.ok(ApiResponse.success(data));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("批量生成ID失败: " + e.getMessage()));
        }
    }
    
    /**
     * 解析ID
     * 
     * @param id 雪花ID
     * @return 包含ID详细信息的响应对象
     */
    @GetMapping("/parse/{id}")
    public ResponseEntity<ApiResponse<IdParseResult>> parse(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("无效的ID"));
        }
        
        try {
            SnowflakeIdWorker.SnowflakeIdInfo info = snowflakeIdWorker.parseId(id);
            
            IdParseResult data = new IdParseResult();
            data.setOriginalId(info.getId());
            data.setTimestamp(info.getTimestamp());
            data.setTimestampStr(formatTimestamp(info.getTimestamp()));
            data.setDatacenterId(info.getDatacenterId());
            data.setWorkerId(info.getWorkerId());
            data.setSequence(info.getSequence());
            
            return ResponseEntity.ok(ApiResponse.success(data));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("解析ID失败: " + e.getMessage()));
        }
    }
    
    /**
     * 性能测试
     * 
     * @param count 测试数量，默认10000个，最多100000个
     * @return 包含性能测试结果的响应对象
     */
    @GetMapping("/performance")
    public ResponseEntity<ApiResponse<PerformanceResult>> performance(
            @RequestParam(defaultValue = "" + DEFAULT_PERFORMANCE_COUNT) int count) {
        
        if (count <= 0) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("测试数量必须大于0"));
        }
        
        int validCount = Math.min(count, MAX_PERFORMANCE_COUNT);
        
        try {
            long startTime = System.currentTimeMillis();
            long firstId = 0;
            long lastId = 0;
            Set<Long> idSet = new HashSet<>(validCount);
            
            for (int i = 0; i < validCount; i++) {
                long id = snowflakeIdWorker.nextId();
                if (i == 0) {
                    firstId = id;
                }
                if (i == validCount - 1) {
                    lastId = id;
                }
                idSet.add(id);
            }
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            long qps = duration > 0 ? (validCount * 1000L / duration) : 0;
            int duplicates = validCount - idSet.size();
            
            PerformanceResult data = new PerformanceResult();
            data.setCount(validCount);
            data.setDuration(duration + "ms");
            data.setQps(qps);
            data.setFirstId(firstId);
            data.setLastId(lastId);
            data.setDuplicates(duplicates);
            
            return ResponseEntity.ok(ApiResponse.success(data));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("性能测试失败: " + e.getMessage()));
        }
    }
    
    /**
     * 并发测试
     * 
     * @param threads 线程数，默认10个，最多50个
     * @param countPerThread 每个线程生成的ID数量，默认1000个，最多10000个
     * @return 包含并发测试结果的响应对象
     */
    @GetMapping("/concurrent")
    public ResponseEntity<ApiResponse<ConcurrentResult>> concurrent(
            @RequestParam(defaultValue = "" + DEFAULT_THREADS) int threads,
            @RequestParam(defaultValue = "" + DEFAULT_COUNT_PER_THREAD) int countPerThread) {
        
        // 参数验证
        if (threads <= 0 || countPerThread <= 0) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("线程数和每线程生成数必须大于0"));
        }
        
        // 限制参数范围
        int validThreads = Math.min(threads, MAX_THREADS);
        int validCountPerThread = Math.min(countPerThread, MAX_COUNT_PER_THREAD);
        
        ExecutorService executor = null;
        try {
            executor = Executors.newFixedThreadPool(validThreads);
            Set<Long> allIds = Collections.synchronizedSet(new HashSet<>());
            CountDownLatch latch = new CountDownLatch(validThreads);
            
            long startTime = System.currentTimeMillis();
            
            for (int i = 0; i < validThreads; i++) {
                executor.submit(() -> {
                    try {
                        for (int j = 0; j < validCountPerThread; j++) {
                            long id = snowflakeIdWorker.nextId();
                            allIds.add(id);
                        }
                    } catch (Exception e) {
                        // 记录异常但继续执行
                        e.printStackTrace();
                    } finally {
                        latch.countDown();
                    }
                });
            }
            
            // 等待所有线程完成
            if (!latch.await(60, TimeUnit.SECONDS)) {
                return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                    .body(ApiResponse.error("并发测试超时"));
            }
            
            long endTime = System.currentTimeMillis();
            
            int totalCount = validThreads * validCountPerThread;
            long duration = endTime - startTime;
            long qps = duration > 0 ? (totalCount * 1000L / duration) : 0;
            int duplicates = totalCount - allIds.size();
            
            ConcurrentResult data = new ConcurrentResult();
            data.setThreads(validThreads);
            data.setCountPerThread(validCountPerThread);
            data.setTotalCount(totalCount);
            data.setUniqueCount(allIds.size());
            data.setDuplicates(duplicates);
            data.setDuration(duration + "ms");
            data.setQps(qps);
            
            return ResponseEntity.ok(ApiResponse.success(data));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("并发测试被中断"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("并发测试失败: " + e.getMessage()));
        } finally {
            // 确保ExecutorService正确关闭
            if (executor != null) {
                executor.shutdown();
                try {
                    if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                        executor.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    executor.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
    
    /**
     * 获取配置信息
     * 
     * @return 包含配置信息的响应对象
     */
    @GetMapping("/info")
    public ResponseEntity<ApiResponse<ConfigInfo>> info() {
        try {
            SnowflakeIdWorker.SnowflakeConfig config = snowflakeIdWorker.getConfig();
            
            ConfigInfo data = new ConfigInfo();
            data.setWorkerId(config.getWorkerId());
            data.setDatacenterId(config.getDatacenterId());
            data.setMaxWorkerId(config.getMaxWorkerId());
            data.setMaxDatacenterId(config.getMaxDatacenterId());
            data.setMaxSequence(config.getMaxSequence());
            data.setDescription("Twitter Snowflake分布式ID生成器");
            data.setIdStructure("1位符号位 + 41位时间戳 + 5位数据中心ID + 5位机器ID + 12位序列号");
            
            return ResponseEntity.ok(ApiResponse.success(data));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("获取配置信息失败: " + e.getMessage()));
        }
    }
    
    /**
     * 格式化时间戳（线程安全）
     * 
     * @param timestamp 时间戳（毫秒）
     * @return 格式化后的时间字符串
     */
    private String formatTimestamp(long timestamp) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(timestamp), 
            ZoneId.systemDefault()
        );
        return dateTime.format(DATE_TIME_FORMATTER);
    }
    
    // ==================== 响应对象定义 ====================
    
    /**
     * 统一API响应对象
     */
    public static class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;
        
        public static <T> ApiResponse<T> success(T data) {
            ApiResponse<T> response = new ApiResponse<>();
            response.setSuccess(true);
            response.setData(data);
            return response;
        }
        
        public static <T> ApiResponse<T> error(String message) {
            ApiResponse<T> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setMessage(message);
            return response;
        }
        
        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public T getData() { return data; }
        public void setData(T data) { this.data = data; }
    }
    
    /**
     * ID生成结果
     */
    public static class IdGenerateResult {
        private long id;
        private String idStr;
        private long timestamp;
        
        // Getters and Setters
        public long getId() { return id; }
        public void setId(long id) { this.id = id; }
        public String getIdStr() { return idStr; }
        public void setIdStr(String idStr) { this.idStr = idStr; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
    
    /**
     * 批量生成结果
     */
    public static class BatchGenerateResult {
        private int count;
        private List<Long> ids;
        
        // Getters and Setters
        public int getCount() { return count; }
        public void setCount(int count) { this.count = count; }
        public List<Long> getIds() { return ids; }
        public void setIds(List<Long> ids) { this.ids = ids; }
    }
    
    /**
     * ID解析结果
     */
    public static class IdParseResult {
        private long originalId;
        private long timestamp;
        private String timestampStr;
        private long datacenterId;
        private long workerId;
        private long sequence;
        
        // Getters and Setters
        public long getOriginalId() { return originalId; }
        public void setOriginalId(long originalId) { this.originalId = originalId; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
        public String getTimestampStr() { return timestampStr; }
        public void setTimestampStr(String timestampStr) { this.timestampStr = timestampStr; }
        public long getDatacenterId() { return datacenterId; }
        public void setDatacenterId(long datacenterId) { this.datacenterId = datacenterId; }
        public long getWorkerId() { return workerId; }
        public void setWorkerId(long workerId) { this.workerId = workerId; }
        public long getSequence() { return sequence; }
        public void setSequence(long sequence) { this.sequence = sequence; }
    }
    
    /**
     * 性能测试结果
     */
    public static class PerformanceResult {
        private int count;
        private String duration;
        private long qps;
        private long firstId;
        private long lastId;
        private int duplicates;
        
        // Getters and Setters
        public int getCount() { return count; }
        public void setCount(int count) { this.count = count; }
        public String getDuration() { return duration; }
        public void setDuration(String duration) { this.duration = duration; }
        public long getQps() { return qps; }
        public void setQps(long qps) { this.qps = qps; }
        public long getFirstId() { return firstId; }
        public void setFirstId(long firstId) { this.firstId = firstId; }
        public long getLastId() { return lastId; }
        public void setLastId(long lastId) { this.lastId = lastId; }
        public int getDuplicates() { return duplicates; }
        public void setDuplicates(int duplicates) { this.duplicates = duplicates; }
    }
    
    /**
     * 并发测试结果
     */
    public static class ConcurrentResult {
        private int threads;
        private int countPerThread;
        private int totalCount;
        private int uniqueCount;
        private int duplicates;
        private String duration;
        private long qps;
        
        // Getters and Setters
        public int getThreads() { return threads; }
        public void setThreads(int threads) { this.threads = threads; }
        public int getCountPerThread() { return countPerThread; }
        public void setCountPerThread(int countPerThread) { this.countPerThread = countPerThread; }
        public int getTotalCount() { return totalCount; }
        public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
        public int getUniqueCount() { return uniqueCount; }
        public void setUniqueCount(int uniqueCount) { this.uniqueCount = uniqueCount; }
        public int getDuplicates() { return duplicates; }
        public void setDuplicates(int duplicates) { this.duplicates = duplicates; }
        public String getDuration() { return duration; }
        public void setDuration(String duration) { this.duration = duration; }
        public long getQps() { return qps; }
        public void setQps(long qps) { this.qps = qps; }
    }
    
    /**
     * 配置信息
     */
    public static class ConfigInfo {
        private long workerId;
        private long datacenterId;
        private long maxWorkerId;
        private long maxDatacenterId;
        private long maxSequence;
        private String description;
        private String idStructure;
        
        // Getters and Setters
        public long getWorkerId() { return workerId; }
        public void setWorkerId(long workerId) { this.workerId = workerId; }
        public long getDatacenterId() { return datacenterId; }
        public void setDatacenterId(long datacenterId) { this.datacenterId = datacenterId; }
        public long getMaxWorkerId() { return maxWorkerId; }
        public void setMaxWorkerId(long maxWorkerId) { this.maxWorkerId = maxWorkerId; }
        public long getMaxDatacenterId() { return maxDatacenterId; }
        public void setMaxDatacenterId(long maxDatacenterId) { this.maxDatacenterId = maxDatacenterId; }
        public long getMaxSequence() { return maxSequence; }
        public void setMaxSequence(long maxSequence) { this.maxSequence = maxSequence; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getIdStructure() { return idStructure; }
        public void setIdStructure(String idStructure) { this.idStructure = idStructure; }
    }
}
