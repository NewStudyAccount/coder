package com.example.demo.otc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/change-primary-number")
@CrossOrigin(origins = "*")
public class ChangePrimaryNumberController {

    @Autowired
    private ChangePrimaryNumberService service;

    @PostMapping("/process")
    public ResponseEntity<?> processChangePrimaryNumber(@RequestBody ChangePrimaryNumberRequest request) {
        try {
            service.processChangePrimaryNumber(request.getOrderId());
            return ResponseEntity.ok("主号码变更触发的成员关系更新处理成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("处理失败: " + e.getMessage());
        }
    }

    public static class ChangePrimaryNumberRequest {
        private Long orderId;

        public Long getOrderId() {
            return orderId;
        }

        public void setOrderId(Long orderId) {
            this.orderId = orderId;
        }
    }
}
