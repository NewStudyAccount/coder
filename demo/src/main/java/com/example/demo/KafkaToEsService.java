package com.example.demo;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

@Service
public class KafkaToEsService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 监听Kafka消息并写入Elasticsearch
     */
    @KafkaListener(topics = "demo-topic", groupId = "flink-es-group")
    public void listenAndInsertToEs(String message) {
        try {
            IndexRequest request = new IndexRequest("flink_cdc_data")
                    .id(UUID.randomUUID().toString())
                    .source(message, XContentType.JSON);
            restHighLevelClient.index(request, RequestOptions.DEFAULT);
            System.out.println("已写入ES: " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}