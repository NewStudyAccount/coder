package com.example.demo;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface UserEsRepository extends ElasticsearchRepository<User, Long> {
    // 可自定义ES查询方法
}