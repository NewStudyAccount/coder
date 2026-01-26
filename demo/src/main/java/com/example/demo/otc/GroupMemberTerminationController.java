package com.example.demo.otc;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/group-member-termination")
public class GroupMemberTerminationController {

    private final GroupMemberTerminationService service;

    public GroupMemberTerminationController(GroupMemberTerminationService service) {
        this.service = service;
    }

    @PostMapping("/process")
    public String process(@RequestBody TerminationRequest request) {
        return service.process(request.getOrderId(), request.getParentSerialNumber(), request.getGroupUserId());
    }

    public static class TerminationRequest {
        private Long orderId;
        private String parentSerialNumber;
        private Long groupUserId;

        public Long getOrderId() {
            return orderId;
        }

        public void setOrderId(Long orderId) {
            this.orderId = orderId;
        }

        public String getParentSerialNumber() {
            return parentSerialNumber;
        }

        public void setParentSerialNumber(String parentSerialNumber) {
            this.parentSerialNumber = parentSerialNumber;
        }

        public Long getGroupUserId() {
            return groupUserId;
        }

        public void setGroupUserId(Long groupUserId) {
            this.groupUserId = groupUserId;
        }
    }
}
