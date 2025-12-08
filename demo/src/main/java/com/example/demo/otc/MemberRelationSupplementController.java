package com.example.demo.otc;

import org.springframework.web.bind.annotation.*;

/**
 * 成员关系补充接口
 * 重构说明：
 * 1. 构造器注入，去除 @Autowired
 * 2. 增加注释
 * 3. 参数判空校验
 * 4. 统一异常处理
 */
@RestController
@RequestMapping("/api/member-relation-supplement")
public class MemberRelationSupplementController {

    private final MemberRelationSupplementService memberRelationSupplementService;

    public MemberRelationSupplementController(MemberRelationSupplementService memberRelationSupplementService) {
        this.memberRelationSupplementService = memberRelationSupplementService;
    }

    /**
     * 补充成员关系
     */
    @PostMapping("/supplement")
    public MemberRelationSupplementResponse supplement(@RequestBody MemberRelationSupplementRequest request) {
        if (request == null) {
            MemberRelationSupplementResponse resp = new MemberRelationSupplementResponse();
            resp.setSuccess(false);
            resp.setMessage("请求体不能为空");
            resp.setInstallLedgers(null);
            resp.setPayRelationLedgers(null);
            return resp;
        }
        try {
            return memberRelationSupplementService.supplement(request);
        } catch (Exception e) {
            MemberRelationSupplementResponse resp = new MemberRelationSupplementResponse();
            resp.setSuccess(false);
            resp.setMessage("系统异常: " + e.getMessage());
            resp.setInstallLedgers(null);
            resp.setPayRelationLedgers(null);
            return resp;
        }
    }
}
