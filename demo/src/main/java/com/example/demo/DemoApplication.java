package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DemoApplication.class);

    @Autowired
    private ProducerService producerService;

    public static void main(String[] args) {
        logger.info("DemoApplication 启动中...");
        SpringApplication.run(DemoApplication.class, args);
        logger.info("DemoApplication 启动完成");
    }

    @Override
    public void run(String... args) throws Exception {
        // 启动时发送一条Kafka测试消息
        try {
            producerService.sendMessage("demo-topic", "Kafka集成测试消息");
            logger.info("已发送Kafka测试消息到 demo-topic");
        } catch (Exception e) {
            logger.error("发送Kafka消息失败", e);
        }
    }
}
