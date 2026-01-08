package com.example.demo.otc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/idd")
@CrossOrigin(origins = "*")
public class IddController {

    @Autowired
    private IddService iddService;

    @PostMapping("/process")
    public ResponseEntity<String> processIdd(@RequestBody IddRequest request) {
        try {
            iddService.processIdd(request.getOrderId(), request.getOrderLineId());
            return ResponseEntity.ok("IDD处理成功");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("IDD处理失败: " + e.getMessage());
        }
    }

    public static class IddRequest {
        private Long orderId;
        private Long orderLineId;

        public Long getOrderId() {
            return orderId;
        }

        public void setOrderId(Long orderId) {
            this.orderId = orderId;
        }

        public Long getOrderLineId() {
            return orderLineId;
        }

        public void setOrderLineId(Long orderLineId) {
            this.orderLineId = orderLineId;
        }
    }
}
