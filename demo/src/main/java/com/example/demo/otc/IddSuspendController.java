package com.example.demo.otc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/idd-suspend")
@CrossOrigin(origins = "*")
public class IddSuspendController {

    @Autowired
    private IddSuspendService iddSuspendService;

    @PostMapping("/process")
    public ResponseEntity<String> processIddSuspend(@RequestBody IddSuspendRequest request) {
        try {
            iddSuspendService.processIddSuspend(request.getOrderId(), request.getOrderLineId(), request.getTradeTypeCode());
            return ResponseEntity.ok("IDD停机业务处理成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("处理失败: " + e.getMessage());
        }
    }

    public static class IddSuspendRequest {
        private Long orderId;
        private Long orderLineId;
        private String tradeTypeCode;

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

        public String getTradeTypeCode() {
            return tradeTypeCode;
        }

        public void setTradeTypeCode(String tradeTypeCode) {
            this.tradeTypeCode = tradeTypeCode;
        }
    }
}
