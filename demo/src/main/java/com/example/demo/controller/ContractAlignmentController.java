package com.example.demo.controller;

import com.example.demo.service.ContractAlignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 合约对齐控制器
 * 提供REST API接口用于对齐非主资费的合约与主资费的UAT
 */
@RestController
@RequestMapping("/api/contract-alignment")
@CrossOrigin(origins = "*")
public class ContractAlignmentController {

    @Autowired
    private ContractAlignmentService contractAlignmentService;

    /**
     * 对齐非主资费的合约与主资费的UAT
     * 
     * @param request 请求参数，包含orderId、orderLineId、srd
     * @return 执行结果
     */
    @PostMapping("/align")
    public ResponseEntity<Map<String, Object>> alignNonMainContractWithMainUat(
            @RequestBody Map<String, String> request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 获取请求参数
            String orderId = request.get("orderId");
            String orderLineId = request.get("orderLineId");
            String srd = request.get("srd");
            
            // 参数校验
            if (orderId == null || orderId.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "订单ID(orderId)不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (orderLineId == null || orderLineId.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "订单行ID(orderLineId)不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (srd == null || srd.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "服务请求日期(srd)不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 验证日期格式（yyyy-MM-dd HH:mm:ss）
            if (!srd.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
                response.put("success", false);
                response.put("message", "服务请求日期(srd)格式不正确，应为：yyyy-MM-dd HH:mm:ss");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 执行业务逻辑
            contractAlignmentService.alignNonMainContractWithMainUat(orderId, orderLineId, srd);
            
            response.put("success", true);
            response.put("message", "合约对齐操作执行成功");
            response.put("data", Map.of(
                "orderId", orderId,
                "orderLineId", orderLineId,
                "srd", srd
            ));
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            // 业务逻辑异常（如参数错误）
            response.put("success", false);
            response.put("message", "参数错误: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
            
        } catch (Exception e) {
            // 系统异常
            response.put("success", false);
            response.put("message", "系统错误: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 批量对齐非主资费的合约与主资费的UAT
     * 
     * @param requests 批量请求参数列表
     * @return 批量执行结果
     */
    @PostMapping("/align-batch")
    public ResponseEntity<Map<String, Object>> alignBatch(
            @RequestBody Map<String, Object> requests) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            @SuppressWarnings("unchecked")
            java.util.List<Map<String, String>> requestList = 
                (java.util.List<Map<String, String>>) requests.get("items");
            
            if (requestList == null || requestList.isEmpty()) {
                response.put("success", false);
                response.put("message", "批量请求参数不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            
            int successCount = 0;
            int failureCount = 0;
            java.util.List<Map<String, Object>> results = new java.util.ArrayList<>();
            
            for (Map<String, String> request : requestList) {
                Map<String, Object> itemResult = new HashMap<>();
                String orderId = request.get("orderId");
                String orderLineId = request.get("orderLineId");
                String srd = request.get("srd");
                
                itemResult.put("orderId", orderId);
                itemResult.put("orderLineId", orderLineId);
                
                try {
                    // 参数校验
                    if (orderId == null || orderLineId == null || srd == null) {
                        throw new IllegalArgumentException("必填参数不能为空");
                    }
                    
                    // 执行业务逻辑
                    contractAlignmentService.alignNonMainContractWithMainUat(orderId, orderLineId, srd);
                    
                    itemResult.put("success", true);
                    itemResult.put("message", "执行成功");
                    successCount++;
                    
                } catch (Exception e) {
                    itemResult.put("success", false);
                    itemResult.put("message", e.getMessage());
                    failureCount++;
                }
                
                results.add(itemResult);
            }
            
            response.put("success", true);
            response.put("message", String.format("批量执行完成，成功: %d, 失败: %d", successCount, failureCount));
            response.put("data", Map.of(
                "total", requestList.size(),
                "successCount", successCount,
                "failureCount", failureCount,
                "details", results
            ));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "批量执行错误: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 健康检查接口
     * 
     * @return 服务状态
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "合约对齐服务运行正常");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
}
