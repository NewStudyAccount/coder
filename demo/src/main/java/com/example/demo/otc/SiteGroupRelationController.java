package com.example.demo.otc;

import org.springframework.web.bind.annotation.*;

/**
 * 站点群组关系接口
 * 重构说明：
 * 1. 构造器注入，去除 @Autowired
 * 2. 增加注释
 * 3. 参数判空校验
 * 4. 统一异常处理
 */
@RestController
@RequestMapping("/api/site-group-relation")
public class SiteGroupRelationController {

    private final SiteGroupRelationService siteGroupRelationService;

    public SiteGroupRelationController(SiteGroupRelationService siteGroupRelationService) {
        this.siteGroupRelationService = siteGroupRelationService;
    }

    /**
     * 修改站点群组关系
     */
    @PostMapping("/modify")
    public SiteGroupRelationResponse modifySiteGroupRelation(@RequestBody SiteGroupRelationRequest request) {
        if (request == null) {
            SiteGroupRelationResponse resp = new SiteGroupRelationResponse();
            resp.setSuccess(false);
            resp.setMessage("请求体不能为空");
            resp.setUuLedgers(null);
            return resp;
        }
        try {
            return siteGroupRelationService.modifySiteGroupRelation(request);
        } catch (Exception e) {
            SiteGroupRelationResponse resp = new SiteGroupRelationResponse();
            resp.setSuccess(false);
            resp.setMessage("系统异常: " + e.getMessage());
            resp.setUuLedgers(null);
            return resp;
        }
    }
}
