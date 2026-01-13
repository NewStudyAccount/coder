package com.example.demo.otc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/add-hunting")
@CrossOrigin(origins = "*")
public class AddHuntingController {

    @Autowired
    private AddHuntingService addHuntingService;

    @PostMapping("/process")
    public ResponseEntity<String> processAddHunting(@RequestBody AddHuntingRequest request) {
        try {
            addHuntingService.processAddHunting(request.getOrderId());
            return ResponseEntity.ok("加装Hunting业务处理成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("处理失败: " + e.getMessage());
        }
    }

    public static class AddHuntingRequest {
        private Long orderId;

        public Long getOrderId() {
            return orderId;
        }

        public void setOrderId(Long orderId) {
            this.orderId = orderId;
        }
    }
}
