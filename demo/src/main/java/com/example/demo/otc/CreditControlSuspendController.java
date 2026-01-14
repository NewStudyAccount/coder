package com.example.demo.otc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/credit-control")
public class CreditControlSuspendController {

    @Autowired
    private CreditControlSuspendService service;

    @PostMapping("/suspend")
    public String suspend(@RequestParam Long accountId, @RequestParam Long orderId) {
        service.processCreditControlSuspend(accountId, orderId);
        return "Process initiated successfully";
    }
}
