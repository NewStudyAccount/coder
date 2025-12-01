package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 通用列表数据接口，支持前端配置字段
 */
@RestController
public class ListDataController {

    /**
     * 获取列表数据
     * @return Map 包含 columns（字段配置）和 data（数据列表）
     */
    @GetMapping("/api/list-data")
    public Map<String, Object> getListData() {
        // 字段配置（可由前端传递，示例为后端返回）
        List<Map<String, String>> columns = Arrays.asList(
                Map.of("key", "user_id", "label", "用户ID"),
                Map.of("key", "trade_type_code", "label", "交易类型"),
                Map.of("key", "scene_type", "label", "场景类型"),
                Map.of("key", "order_id", "label", "订单ID")
        );

        // mock数据
        List<Map<String, Object>> data = new ArrayList<>();
        data.add(Map.of("user_id", "U001", "trade_type_code", "340", "scene_type", "34000", "order_id", "O1001"));
        data.add(Map.of("user_id", "U002", "trade_type_code", "192", "scene_type", "19201", "order_id", "O1002"));
        data.add(Map.of("user_id", "U003", "trade_type_code", "10", "scene_type", "10000", "order_id", "O1003"));

        Map<String, Object> result = new HashMap<>();
        result.put("columns", columns);
        result.put("data", data);
        return result;
    }
}