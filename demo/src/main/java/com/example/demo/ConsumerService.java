package com.example.demo;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsumerService {

    /**
     * 监听Kafka topic，消费消息（单条自动ack）
     */
    @KafkaListener(topics = "demo-topic", groupId = "demo-group")
    public void listen(String message) {
        System.out.println("收到Kafka消息: " + message);
    }

    /**
     * 批量消费Kafka消息（自动ack）
     */
    @KafkaListener(topics = "demo-topic", groupId = "demo-group", containerFactory = "batchFactory")
    public void batchListen(List<String> messages) {
        System.out.println("批量收到Kafka消息: " + messages);
    }

    /**
     * 手动ack消费Kafka消息
     */
    @KafkaListener(topics = "demo-topic", groupId = "demo-group", containerFactory = "manualAckFactory")
    public void manualAckListen(String message, Acknowledgment ack) {
        System.out.println("手动ack收到Kafka消息: " + message);
        // 业务处理完成后手动提交offset
        ack.acknowledge();
    }
}
