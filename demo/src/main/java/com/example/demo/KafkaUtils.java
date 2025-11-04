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
        kafkaTemplate.send(topic, message);
    }

    /**
     * 异步发送消息（带回调）
     */
    public static void sendAsync(String topic, String message, ListenableFutureCallback<SendResult<String, String>> callback) {
        checkTemplate();
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, message);
        future.addCallback(callback);
    }

    /**
     * 批量异步发送消息
     */
    public static void sendBatchAsync(String topic, List<String> messages) {
        checkTemplate();
        for (String msg : messages) {
            kafkaTemplate.send(topic, msg);
        }
    }

    /**
     * 同步发送消息（带超时）
     */
    public static boolean sendSync(String topic, String message, long timeoutMillis) {
        checkTemplate();
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, message);
        try {
            SendResult<String, String> result = future.get(timeoutMillis, TimeUnit.MILLISECONDS);
            RecordMetadata meta = result.getRecordMetadata();
            return meta != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 发送消息到指定分区
     */
    public static void sendToPartition(String topic, int partition, String key, String message) {
        checkTemplate();
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
            throw new IllegalStateException("KafkaTemplate未初始化，请先调用KafkaUtils.init()注入KafkaTemplate实例");
        }
    }
}
