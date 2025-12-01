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
            System.err.println("EsUtils.checkTemplate: ElasticsearchRestTemplate未初始化");
            throw new IllegalStateException("ElasticsearchRestTemplate未初始化，请先调用EsUtils.init()注入ElasticsearchRestTemplate实例");
        }
    }

    /** 新增或更新文档 */
    public static <T> String save(String index, T entity) {
        checkTemplate();
        if (CommonUtils.isEmpty(index) || entity == null) {
            throw new IllegalArgumentException("index不能为空，entity不能为null");
        }
        IndexQuery query = new IndexQueryBuilder()
                .withObject(entity)
                .build();
        return esTemplate.index(query, esTemplate.getIndexCoordinatesFor(index));
    }

    /** 根据id查询文档 */
    public static <T> T getById(String index, String id, Class<T> clazz) {
        checkTemplate();
        if (CommonUtils.isEmpty(index) || CommonUtils.isEmpty(id) || clazz == null) {
            throw new IllegalArgumentException("index、id不能为空，clazz不能为null");
        }
        return esTemplate.get(id, clazz, esTemplate.getIndexCoordinatesFor(index));
    }

    /** 删除文档 */
    public static void deleteById(String index, String id) {
        checkTemplate();
        if (CommonUtils.isEmpty(index) || CommonUtils.isEmpty(id)) {
            throw new IllegalArgumentException("index、id不能为空");
        }
        esTemplate.delete(id, esTemplate.getIndexCoordinatesFor(index));
    }

    /** 搜索文档 */
    public static <T> List<T> search(String index, Query query, Class<T> clazz) {
        checkTemplate();
        if (CommonUtils.isEmpty(index) || query == null || clazz == null) {
            throw new IllegalArgumentException("index不能为空，query和clazz不能为null");
        }
        SearchHits<T> hits = esTemplate.search(query, clazz, esTemplate.getIndexCoordinatesFor(index));
        // 兼容 Java 8
        List<T> result = new java.util.ArrayList<>();
        hits.forEach(hit -> result.add(hit.getContent()));
        return result;
    }

    /** 判断索引是否存在 */
    public static boolean indexExists(String index) {
        checkTemplate();
        if (CommonUtils.isEmpty(index)) {
            throw new IllegalArgumentException("index不能为空");
        }
        IndexOperations ops = esTemplate.indexOps(esTemplate.getIndexCoordinatesFor(index));
        return ops.exists();
    }

    /** 创建索引 */
    public static boolean createIndex(String index) {
        checkTemplate();
        if (CommonUtils.isEmpty(index)) {
            throw new IllegalArgumentException("index不能为空");
        }
        IndexOperations ops = esTemplate.indexOps(esTemplate.getIndexCoordinatesFor(index));
        return ops.create();
    }

    /** 删除索引 */
    public static boolean deleteIndex(String index) {
        checkTemplate();
        if (CommonUtils.isEmpty(index)) {
            throw new IllegalArgumentException("index不能为空");
        }
        IndexOperations ops = esTemplate.indexOps(esTemplate.getIndexCoordinatesFor(index));
        return ops.delete();
    }
}
