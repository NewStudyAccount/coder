package com.example.demo.otc;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * IDAP 取消号段并连带对散号拆机 控制器（Mock）
 * 提供后端接口：POST /api/idap/cancel-segment/execute
 * 入参：orderId（Long），userId（String）
 * 出参：IdapCancelSegmentResponse（成功标记、创建的拆机行、编排步骤）
 */
@RestController
@RequestMapping("/api/idap/cancel-segment")
public class IdapCancelSegmentController {

    private final IdapCancelSegmentService service;

    public IdapCancelSegmentController(IdapCancelSegmentService service) {
        this.service = service;
    }

    /**
     * 执行取消号段并连带散号拆机流程
     */
    @PostMapping("/execute")
    public IdapCancelSegmentService.IdapCancelSegmentResponse execute(@RequestBody IdapCancelSegmentService.IdapCancelSegmentRequest req) {
        if (req == null || req.getOrderId() == null || req.getUserId() == null) {
            IdapCancelSegmentService.IdapCancelSegmentResponse resp = new IdapCancelSegmentService.IdapCancelSegmentResponse();
            resp.setSuccess(false);
            resp.setMessage("参数错误：orderId/userId 不能为空");
            resp.setCreatedLines(null);
            resp.setBuildSteps(null);
            return resp;
        }
        return service.execute(req);
    }

    /**
     * 简单的演示数据查看（可选）
     */
    @GetMapping("/demo-request")
    public Map<String, Object> demoRequest() {
        Map<String, Object> m = new HashMap<>();
        m.put("orderId", 1001L);
        m.put("userId", "U-GROUP-001");
        return m;
    }
}