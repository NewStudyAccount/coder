package com.example.demo.otc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/site-group-relation")
public class SiteGroupRelationController {

    @Autowired
    private SiteGroupRelationService siteGroupRelationService;

    @PostMapping("/modify")
    public SiteGroupRelationResponse modifySiteGroupRelation(@RequestBody SiteGroupRelationRequest request) {
        return siteGroupRelationService.modifySiteGroupRelation(request);
    }
}