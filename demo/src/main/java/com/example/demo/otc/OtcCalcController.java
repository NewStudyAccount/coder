package com.example.demo.otc;

import com.example.demo.otc.OtcCalcModels.OtcCalcRequest;
import com.example.demo.otc.OtcCalcModels.OtcCalcResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/otc")
public class OtcCalcController {

    private final OtcCalcService service;

    public OtcCalcController(OtcCalcService service) {
        this.service = service;
    }

    @PostMapping("/calc")
    public OtcCalcResponse calc(@RequestBody OtcCalcRequest req) {
        return service.calculate(req);
    }
}