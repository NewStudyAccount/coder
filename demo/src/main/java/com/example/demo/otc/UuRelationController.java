package com.example.demo.otc;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/uu-relation")
public class UuRelationController {

    private final UuRelationService service;

    public UuRelationController(UuRelationService service) {
        this.service = service;
    }

    @PostMapping("/generate")
    public UuRelationResponse generate(@RequestBody UuRelationRequest req) {
        return service.generateUuLedger(req);
    }
}
