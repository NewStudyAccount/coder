package com.example.demo.otc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member-relation-supplement")
public class MemberRelationSupplementController {

    @Autowired
    private MemberRelationSupplementService memberRelationSupplementService;

    @PostMapping("/supplement")
    public MemberRelationSupplementResponse supplement(@RequestBody MemberRelationSupplementRequest request) {
        return memberRelationSupplementService.supplement(request);
    }
}