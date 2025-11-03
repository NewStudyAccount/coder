package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

/**
 * Kafka工具类，封装常用消息发送操作
 */
@Component
public class KafkaUtils {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 发送消息（异步）
     */
    public void sendAsync(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }

    /**
     * 发送消息（同步，带超时）
     */
    public boolean sendSync(String topic, String message, long timeoutMillis) {
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
    public void sendToPartition(String topic, int partition, String key, String message) {
        kafkaTemplate.send(topic, partition, key, message);
    }
}