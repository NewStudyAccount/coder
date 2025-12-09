package com.example.demo.otc;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * change group 控制器（Mock）
 * 提供后端接口：POST /api/change-group/execute
 * 入参：orderId（Long）、orderLineId（Long）
 * 出参：ChangeGroupResponse（成功标记、创建的订单/台账、步骤说明等）
 */
@RestController
@RequestMapping("/api/change-group")
public class ChangeGroupController {

    private final ChangeGroupService service;

    public ChangeGroupController(ChangeGroupService service) {
        this.service = service;
    }

    /**
     * 执行群组用户过户（101）/移机（3410）连带增补成员订单流程
     */
    @PostMapping("/execute")
    public ChangeGroupService.ChangeGroupResponse execute(@RequestBody ChangeGroupService.ChangeGroupRequest req) {
        if (req == null || req.orderId == null || req.orderLineId == null) {
            ChangeGroupService.ChangeGroupResponse resp = new ChangeGroupService.ChangeGroupResponse();
            resp.success = false;
            resp.message = "参数错误：orderId/orderLineId 不能为空";
            resp.steps = null;
            return resp;
        }
        return service.execute(req);
    }

    /**
     * 简单的演示入参查看（可选）
     */
    @GetMapping("/demo-request")
    public Map<String, Object> demoRequest() {
        Map<String, Object> m = new HashMap<>();
        m.put("orderId", 9001L);
        m.put("orderLineId", 8001L);
        return m;
    }
}