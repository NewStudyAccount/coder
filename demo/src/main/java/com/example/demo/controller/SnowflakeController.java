package com.example.demo.controller;

import com.example.demo.SnowflakeIdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * 雪花算法使用示例Controller
 * 演示如何在实际项目中使用雪花算法生成分布式ID
 */
@RestController
@RequestMapping("/api/snowflake")
public class SnowflakeController {

    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;

    /**
     * 生成单个ID
     * 
     * @return 生成的分布式ID
     */
    @GetMapping("/generate")
    public Map<String, Object> generateId() {
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
     * 
     * @param count 生成数量，默认10个，最多1000个
     * @return 生成的ID列表
     */
    @GetMapping("/batch")
    public Map<String, Object> batchGenerate(@RequestParam(defaultValue = "10") int count) {
        if (count <= 0 || count > 1000) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "生成数量必须在1-1000之间");
            return error;
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
     * 解析ID，查看ID的组成部分
     * 
     * @param id 要解析的ID
     * @return ID的各个组成部分
     */
    @GetMapping("/parse/{id}")
    public Map<String, Object> parseId(@PathVariable Long id) {
        try {
            Map<String, Object> parsed = snowflakeIdWorker.parseId(id);
            parsed.put("success", true);
            return parsed;
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "ID解析失败: " + e.getMessage());
            return error;
        }
    }

    /**
     * 性能测试：生成指定数量的ID并统计耗时
     * 
     * @param count 测试数量，默认10000个，最多100000个
     * @return 性能测试结果
     */
    @GetMapping("/performance")
    public Map<String, Object> performanceTest(@RequestParam(defaultValue = "10000") int count) {
        if (count <= 0 || count > 100000) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "测试数量必须在1-100000之间");
            return error;
        }

        long startTime = System.currentTimeMillis();
        List<Long> ids = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            ids.add(snowflakeIdWorker.nextId());
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("count", count);
        result.put("duration", duration + "ms");
        result.put("qps", duration > 0 ? (count * 1000 / duration) : count);
        result.put("firstId", ids.get(0));
        result.put("lastId", ids.get(ids.size() - 1));
        
        // 检查是否有重复ID
        Set<Long> uniqueIds = new HashSet<>(ids);
        result.put("duplicates", count - uniqueIds.size());
        
        return result;
    }

    /**
     * 并发测试：多线程并发生成ID
     * 
     * @param threads 线程数，默认10个，最多50个
     * @param countPerThread 每个线程生成的ID数量，默认1000个，最多10000个
     * @return 并发测试结果
     */
    @GetMapping("/concurrent")
    public Map<String, Object> concurrentTest(
            @RequestParam(defaultValue = "10") int threads,
            @RequestParam(defaultValue = "1000") int countPerThread) {
        
        if (threads <= 0 || threads > 50) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "线程数必须在1-50之间");
            return error;
        }
        
        if (countPerThread <= 0 || countPerThread > 10000) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "每线程生成数量必须在1-10000之间");
            return error;
        }

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);
        Set<Long> allIds = Collections.synchronizedSet(new HashSet<>());
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < threads; i++) {
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
        long duration = endTime - startTime;
        int totalCount = threads * countPerThread;
        
        executor.shutdown();
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("threads", threads);
        result.put("countPerThread", countPerThread);
        result.put("totalCount", totalCount);
        result.put("uniqueCount", allIds.size());
        result.put("duplicates", totalCount - allIds.size());
        result.put("duration", duration + "ms");
        result.put("qps", duration > 0 ? (totalCount * 1000 / duration) : totalCount);
        
        return result;
    }

    /**
     * 获取雪花算法配置信息
     * 
     * @return 配置信息
     */
    @GetMapping("/info")
    public Map<String, Object> getInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("success", true);
        info.put("workerId", snowflakeIdWorker.getWorkerId());
        info.put("datacenterId", snowflakeIdWorker.getDatacenterId());
        info.put("maxWorkerId", 31);
        info.put("maxDatacenterId", 31);
        info.put("maxSequence", 4095);
        info.put("description", "Twitter Snowflake分布式ID生成器");
        info.put("idStructure", "1位符号位 + 41位时间戳 + 5位数据中心ID + 5位机器ID + 12位序列号");
        return info;
    }
}
