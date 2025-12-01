package com.example.demo;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Kafka工具类，封装常用消息发送与消费操作，支持静态调用。
 */
public class KafkaUtils {

    private static KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 初始化KafkaTemplate，需在应用启动时调用一次
     */
    public static void init(KafkaTemplate<String, String> template) {
        kafkaTemplate = template;
    }

    /**
     * 异步发送消息
     */
    public static void sendAsync(String topic, String message) {
        checkTemplate();
        if (CommonUtils.isEmpty(topic) || CommonUtils.isEmpty(message)) {
            throw new IllegalArgumentException("topic和message不能为空");
        }
        kafkaTemplate.send(topic, message);
    }

    /**
     * 异步发送消息（带回调）
     */
    public static void sendAsync(String topic, String message, ListenableFutureCallback<SendResult<String, String>> callback) {
        checkTemplate();
        if (CommonUtils.isEmpty(topic) || CommonUtils.isEmpty(message) || callback == null) {
            throw new IllegalArgumentException("topic、message不能为空，callback不能为null");
        }
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, message);
        future.addCallback(callback);
    }

    /**
     * 批量异步发送消息
     */
    public static void sendBatchAsync(String topic, List<String> messages) {
        checkTemplate();
        if (CommonUtils.isEmpty(topic) || messages == null || messages.isEmpty()) {
            throw new IllegalArgumentException("topic不能为空，messages不能为null或空");
        }
        for (String msg : messages) {
            kafkaTemplate.send(topic, msg);
        }
    }

    /**
     * 同步发送消息（带超时）
     */
    public static boolean sendSync(String topic, String message, long timeoutMillis) {
        checkTemplate();
        if (CommonUtils.isEmpty(topic) || CommonUtils.isEmpty(message) || timeoutMillis <= 0) {
            throw new IllegalArgumentException("topic、message不能为空，timeoutMillis需大于0");
        }
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, message);
        try {
            SendResult<String, String> result = future.get(timeoutMillis, TimeUnit.MILLISECONDS);
            RecordMetadata meta = result.getRecordMetadata();
            return meta != null;
        } catch (Exception e) {
            System.err.println("KafkaUtils.sendSync error: " + e.getMessage());
            return false;
        }
    }

    /**
     * 发送消息到指定分区
     */
    public static void sendToPartition(String topic, int partition, String key, String message) {
        checkTemplate();
        if (CommonUtils.isEmpty(topic) || CommonUtils.isEmpty(key) || CommonUtils.isEmpty(message)) {
            throw new IllegalArgumentException("topic、key、message不能为空");
        }
        kafkaTemplate.send(topic, partition, key, message);
    }

    /**
     * 简单消费消息示例（实际消费应通过@KafkaListener实现，此处仅工具方法示例）
     */
    public static String extractMessage(ConsumerRecord<String, String> record) {
        if (record == null) return null;
        return record.value();
    }

    private static void checkTemplate() {
        if (kafkaTemplate == null) {
            System.err.println("KafkaUtils.checkTemplate: KafkaTemplate未初始化");
            throw new IllegalStateException("KafkaTemplate未初始化，请先调用KafkaUtils.init()注入KafkaTemplate实例");
        }
    }
}
