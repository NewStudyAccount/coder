package com.example.demo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * MongoDB 工具类，封装常用操作
 * 依赖：org.mongodb:mongodb-driver
 */
public class MongoDBUtils {

    private static MongoClient mongoClient = null;
    private static MongoDatabase database = null;

    /**
     * 初始化MongoDB连接
     * @param uri 连接字符串，如 mongodb://localhost:27017
     * @param dbName 数据库名
     */
    public static void init(String uri, String dbName) {
        if (CommonUtils.isEmpty(uri) || CommonUtils.isEmpty(dbName)) {
            throw new IllegalArgumentException("uri和dbName不能为空");
        }
        if (mongoClient == null) {
            mongoClient = new MongoClient(new MongoClientURI(uri));
            database = mongoClient.getDatabase(dbName);
        }
    }

    /**
     * 获取集合
     */
    public static MongoCollection<Document> getCollection(String collectionName) {
        if (database == null) throw new IllegalStateException("MongoDB未初始化，请先调用MongoDBUtils.init()");
        if (CommonUtils.isEmpty(collectionName)) throw new IllegalArgumentException("collectionName不能为空");
        return database.getCollection(collectionName);
    }

    /**
     * 插入文档
     */
    public static void insert(String collectionName, Document doc) {
        if (doc == null) throw new IllegalArgumentException("doc不能为null");
        getCollection(collectionName).insertOne(doc);
    }

    /**
     * 查询所有文档
     */
    public static List<Document> findAll(String collectionName) {
        List<Document> list = new ArrayList<>();
        for (Document doc : getCollection(collectionName).find()) {
            list.add(doc);
        }
        return list;
    }

    /**
     * 根据条件查询
     */
    public static List<Document> find(String collectionName, Document filter) {
        if (filter == null) throw new IllegalArgumentException("filter不能为null");
        List<Document> list = new ArrayList<>();
        for (Document doc : getCollection(collectionName).find(filter)) {
            list.add(doc);
        }
        return list;
    }

    /**
     * 更新文档
     */
    public static void update(String collectionName, Document filter, Document update) {
        if (filter == null || update == null) throw new IllegalArgumentException("filter和update不能为null");
        getCollection(collectionName).updateMany(filter, new Document("$set", update));
    }

    /**
     * 删除文档
     */
    public static void delete(String collectionName, Document filter) {
        if (filter == null) throw new IllegalArgumentException("filter不能为null");
        getCollection(collectionName).deleteMany(filter);
    }

    /**
     * 关闭连接
     */
    public static void close() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
            database = null;
            System.out.println("MongoDB连接已关闭");
        }
    }
}