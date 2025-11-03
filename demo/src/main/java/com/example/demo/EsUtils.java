package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Elasticsearch工具类，封装常用操作
 */
@Component
public class EsUtils {

    @Autowired
    private ElasticsearchRestTemplate esTemplate;

    /**
     * 新增或更新文档
     */
    public <T> String save(String index, T entity) {
        IndexQuery query = new IndexQueryBuilder()
                .withObject(entity)
                .build();
        return esTemplate.index(query, esTemplate.getIndexCoordinatesFor(index));
    }

    /**
     * 根据id查询文档
     */
    public <T> T getById(String index, String id, Class<T> clazz) {
        return esTemplate.get(id, clazz, esTemplate.getIndexCoordinatesFor(index));
    }

    /**
     * 删除文档
     */
    public void deleteById(String index, String id) {
        esTemplate.delete(id, esTemplate.getIndexCoordinatesFor(index));
    }

    /**
     * 搜索文档
     */
    public <T> List<T> search(String index, Query query, Class<T> clazz) {
        SearchHits<T> hits = esTemplate.search(query, clazz, esTemplate.getIndexCoordinatesFor(index));
        return hits.stream().map(hit -> hit.getContent()).toList();
    }

    /**
     * 判断索引是否存在
     */
    public boolean indexExists(String index) {
        IndexOperations ops = esTemplate.indexOps(esTemplate.getIndexCoordinatesFor(index));
        return ops.exists();
    }

    /**
     * 创建索引
     */
    public boolean createIndex(String index) {
        IndexOperations ops = esTemplate.indexOps(esTemplate.getIndexCoordinatesFor(index));
        return ops.create();
    }

    /**
     * 删除索引
     */
    public boolean deleteIndex(String index) {
        IndexOperations ops = esTemplate.indexOps(esTemplate.getIndexCoordinatesFor(index));
        return ops.delete();
    }
}