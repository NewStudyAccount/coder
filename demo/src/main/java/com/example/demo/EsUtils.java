package com.example.demo;

import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.*;
import java.util.List;

/**
 * Elasticsearch工具类，封装常用操作，支持静态调用。
 */
public class EsUtils {

    private static ElasticsearchRestTemplate esTemplate;

    /**
     * 初始化ElasticsearchRestTemplate，需在应用启动时调用一次
     */
    public static void init(ElasticsearchRestTemplate template) {
        esTemplate = template;
    }

    private static void checkTemplate() {
        if (esTemplate == null) {
            throw new IllegalStateException("ElasticsearchRestTemplate未初始化，请先调用EsUtils.init()注入ElasticsearchRestTemplate实例");
        }
    }

    /** 新增或更新文档 */
    public static <T> String save(String index, T entity) {
        checkTemplate();
        IndexQuery query = new IndexQueryBuilder()
                .withObject(entity)
                .build();
        return esTemplate.index(query, esTemplate.getIndexCoordinatesFor(index));
    }

    /** 根据id查询文档 */
    public static <T> T getById(String index, String id, Class<T> clazz) {
        checkTemplate();
        return esTemplate.get(id, clazz, esTemplate.getIndexCoordinatesFor(index));
    }

    /** 删除文档 */
    public static void deleteById(String index, String id) {
        checkTemplate();
        esTemplate.delete(id, esTemplate.getIndexCoordinatesFor(index));
    }

    /** 搜索文档 */
    public static <T> List<T> search(String index, Query query, Class<T> clazz) {
        checkTemplate();
        SearchHits<T> hits = esTemplate.search(query, clazz, esTemplate.getIndexCoordinatesFor(index));
        return hits.stream().map(hit -> hit.getContent()).toList();
    }

    /** 判断索引是否存在 */
    public static boolean indexExists(String index) {
        checkTemplate();
        IndexOperations ops = esTemplate.indexOps(esTemplate.getIndexCoordinatesFor(index));
        return ops.exists();
    }

    /** 创建索引 */
    public static boolean createIndex(String index) {
        checkTemplate();
        IndexOperations ops = esTemplate.indexOps(esTemplate.getIndexCoordinatesFor(index));
        return ops.create();
    }

    /** 删除索引 */
    public static boolean deleteIndex(String index) {
        checkTemplate();
        IndexOperations ops = esTemplate.indexOps(esTemplate.getIndexCoordinatesFor(index));
        return ops.delete();
    }
}
