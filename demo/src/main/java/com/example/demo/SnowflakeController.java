package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

/**
 * 雪花算法 REST API 控制器
 * 提供ID生成、解析、性能测试等接口
 */
@RestController
@RequestMapping("/api/snowflake")
@CrossOrigin(origins = "*")
public class SnowflakeController {
    
    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;
    
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    /**
     * 生成单个ID
     */
    @GetMapping("/generate")
    public Map<String, Object> generate() {
        long id = snowflakeIdWorker.nextId();
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("id", id);
        result.put("idStr", String.valueOf(id));
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }
    
    /**
     * 批量生成ID
     * @param count 生成数量，默认10个，最多1000个
     */
    @GetMapping("/batch")
    public Map<String, Object> batch(@RequestParam(defaultValue = "10") int count) {
        if (count > 1000) {
            count = 1000;
        }
        
        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ids.add(snowflakeIdWorker.nextId());
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("count", count);
        result.put("ids", ids);
        return result;
    }
    
    /**
     * 解析ID
     * @param id 雪花ID
     */
    @GetMapping("/parse/{id}")
    public Map<String, Object> parse(@PathVariable Long id) {
        SnowflakeIdWorker.SnowflakeIdInfo info = snowflakeIdWorker.parseId(id);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("originalId", info.getId());
        result.put("timestamp", info.getTimestamp());
        result.put("timestampStr", sdf.format(new Date(info.getTimestamp())));
        result.put("datacenterId", info.getDatacenterId());
        result.put("workerId", info.getWorkerId());
        result.put("sequence", info.getSequence());
        return result;
    }
    
    /**
     * 性能测试
     * @param count 测试数量，默认10000个，最多100000个
     */
    @GetMapping("/performance")
    public Map<String, Object> performance(@RequestParam(defaultValue = "10000") int count) {
        if (count > 100000) {
            count = 100000;
        }
        
        long startTime = System.currentTimeMillis();
        long firstId = 0;
        long lastId = 0;
        Set<Long> idSet = new HashSet<>();
        
        for (int i = 0; i < count; i++) {
            long id = snowflakeIdWorker.nextId();
            if (i == 0) {
                firstId = id;
            }
            if (i == count - 1) {
                lastId = id;
            }
            idSet.add(id);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        long qps = duration > 0 ? (count * 1000L / duration) : 0;
        int duplicates = count - idSet.size();
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("count", count);
        result.put("duration", duration + "ms");
        result.put("qps", qps);
        result.put("firstId", firstId);
        result.put("lastId", lastId);
        result.put("duplicates", duplicates);
        return result;
    }
    
    /**
     * 并发测试
     * @param threads 线程数，默认10个，最多50个
     * @param countPerThread 每个线程生成的ID数量，默认1000个，最多10000个
     */
    @GetMapping("/concurrent")
    public Map<String, Object> concurrent(
            @RequestParam(defaultValue = "10") int threads,
            @RequestParam(defaultValue = "1000") int countPerThread) {
        
        if (threads > 50) {
            threads = 50;
        }
        if (countPerThread > 10000) {
            countPerThread = 10000;
        }
        
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        Set<Long> allIds = Collections.synchronizedSet(new HashSet<>());
        CountDownLatch latch = new CountDownLatch(threads);
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < threads; i++) {
            final int threadIndex = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < countPerThread; j++) {
                        long id = snowflakeIdWorker.nextId();
                        allIds.add(id);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        long endTime = System.currentTimeMillis();
        executor.shutdown();
        
        int totalCount = threads * countPerThread;
        long duration = endTime - startTime;
        long qps = duration > 0 ? (totalCount * 1000L / duration) : 0;
        int duplicates = totalCount - allIds.size();
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("threads", threads);
        result.put("countPerThread", countPerThread);
        result.put("totalCount", totalCount);
        result.put("uniqueCount", allIds.size());
        result.put("duplicates", duplicates);
        result.put("duration", duration + "ms");
        result.put("qps", qps);
        return result;
    }
    
    /**
     * 获取配置信息
     */
    @GetMapping("/info")
    public Map<String, Object> info() {
        SnowflakeIdWorker.SnowflakeConfig config = snowflakeIdWorker.getConfig();
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("workerId", config.getWorkerId());
        result.put("datacenterId", config.getDatacenterId());
        result.put("maxWorkerId", config.getMaxWorkerId());
        result.put("maxDatacenterId", config.getMaxDatacenterId());
        result.put("maxSequence", config.getMaxSequence());
        result.put("description", "Twitter Snowflake分布式ID生成器");
        result.put("idStructure", "1位符号位 + 41位时间戳 + 5位数据中心ID + 5位机器ID + 12位序列号");
        return result;
    }
}
