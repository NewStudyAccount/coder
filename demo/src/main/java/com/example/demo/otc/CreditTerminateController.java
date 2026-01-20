package com.example.demo.otc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/credit-terminate")
@CrossOrigin(origins = "*")
public class CreditTerminateController {

    @Autowired
    private CreditTerminateService creditTerminateService;

    @PostMapping("/process")
    public ResponseEntity<?> processCreditTerminate(@RequestBody CreditTerminateRequest request) {
        try {
            CreditTerminateResult result = creditTerminateService.processCreditTerminate(request.getOrderId(), request.getOrderLineId());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("处理失败: " + e.getMessage());
        }
    }

    public static class CreditTerminateRequest {
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
    
    @PostMapping("/process-completion")
    public ResponseEntity<?> processCompletionMessage(@RequestBody CompletionRequest request) {
        try {
            creditTerminateService.processCompletionMessage(request.getOrderId());
            return ResponseEntity.ok("竣工消息处理成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("处理失败: " + e.getMessage());
        }
    }
    
    public static class CompletionRequest {
        private Long orderId;
        
        public Long getOrderId() {
            return orderId;
        }
        
        public void setOrderId(Long orderId) {
            this.orderId = orderId;
        }
    }
}
