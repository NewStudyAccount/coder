package com.example.demo.otc;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user-relation")
public class UserRelationController {

    private final UserRelationService service;

    public UserRelationController(UserRelationService service) {
        this.service = service;
    }

    @PostMapping("/generate")
    public UserRelationResponse generate(@RequestBody UserRelationRequest req) {
        return service.generateUserLedger(req);
    }
}
