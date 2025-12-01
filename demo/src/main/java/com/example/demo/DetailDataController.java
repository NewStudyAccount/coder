package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 详情数据接口
 */
@RestController
public class DetailDataController {

    /**
     * 查询详情数据
     * @param orderId 订单ID
     * @return 详情信息
     */
    @GetMapping("/api/detail-data")
    public Map<String, Object> getDetailData(@RequestParam("order_id") String orderId) {
        // mock 详情数据
        Map<String, Object> detail = new HashMap<>();
        detail.put("order_id", orderId);
        detail.put("user_id", "U001");
        detail.put("trade_type_code", "340");
        detail.put("scene_type", "34000");
        detail.put("amount", 123.45);
        detail.put("status", "已完成");
        detail.put("create_time", "2025-12-01 15:00:00");
        detail.put("remark", "订单详情演示数据");

        return detail;
    }
}