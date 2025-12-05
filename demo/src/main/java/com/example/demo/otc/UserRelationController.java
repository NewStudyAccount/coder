package com.example.demo.otc;

import org.springframework.web.bind.annotation.*;

/**
 * 用户关系台账接口
 * 重构说明：
 * 1. 增加注释
 * 2. 参数判空校验
 * 3. 统一异常处理
 */
@RestController
@RequestMapping("/api/user-relation")
public class UserRelationController {

    private final UserRelationService service;

    public UserRelationController(UserRelationService service) {
        this.service = service;
    }

    /**
     * 生成用户关系台账
     */
    @PostMapping("/generate")
    public UserRelationResponse generate(@RequestBody UserRelationRequest req) {
        if (req == null) {
            UserRelationResponse resp = new UserRelationResponse();
            resp.success = false;
            resp.message = "请求体不能为空";
            resp.ledger = null;
            return resp;
        }
        try {
            return service.generateUserLedger(req);
        } catch (Exception e) {
            UserRelationResponse resp = new UserRelationResponse();
            resp.success = false;
            resp.message = "系统异常: " + e.getMessage();
            resp.ledger = null;
            return resp;
        }
    }
}
