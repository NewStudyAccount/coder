package com.example.demo.otc;

import java.util.List;

public class SiteGroupRelationResponse {
    private boolean success;
    private String message;
    private List<UuLedgerDto> uuLedgers;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<UuLedgerDto> getUuLedgers() {
        return uuLedgers;
    }

    public void setUuLedgers(List<UuLedgerDto> uuLedgers) {
        this.uuLedgers = uuLedgers;
    }

    public static class UuLedgerDto {
        private Long orderId;
        private Long orderLineId;
        private String relationTypeCode;
        private String userIdA;
        private String userIdB;
        private String serialNumberA;
        private String serialNumberB;
        private String callSequence;
        private String isPrimaryNumber;
        private String roleCodeA;
        private String roleCodeB;
        private String modifyTag;
        private String startDate;
        private String endDate;

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

        public String getRelationTypeCode() {
            return relationTypeCode;
        }

        public void setRelationTypeCode(String relationTypeCode) {
            this.relationTypeCode = relationTypeCode;
        }

        public String getUserIdA() {
            return userIdA;
        }

        public void setUserIdA(String userIdA) {
            this.userIdA = userIdA;
        }

        public String getUserIdB() {
            return userIdB;
        }

        public void setUserIdB(String userIdB) {
            this.userIdB = userIdB;
        }

        public String getSerialNumberA() {
            return serialNumberA;
        }

        public void setSerialNumberA(String serialNumberA) {
            this.serialNumberA = serialNumberA;
        }

        public String getSerialNumberB() {
            return serialNumberB;
        }

        public void setSerialNumberB(String serialNumberB) {
            this.serialNumberB = serialNumberB;
        }

        public String getCallSequence() {
            return callSequence;
        }

        public void setCallSequence(String callSequence) {
            this.callSequence = callSequence;
        }

        public String getIsPrimaryNumber() {
            return isPrimaryNumber;
        }

        public void setIsPrimaryNumber(String isPrimaryNumber) {
            this.isPrimaryNumber = isPrimaryNumber;
        }

        public String getRoleCodeA() {
            return roleCodeA;
        }

        public void setRoleCodeA(String roleCodeA) {
            this.roleCodeA = roleCodeA;
        }

        public String getRoleCodeB() {
            return roleCodeB;
        }

        public void setRoleCodeB(String roleCodeB) {
            this.roleCodeB = roleCodeB;
        }

        public String getModifyTag() {
            return modifyTag;
        }

        public void setModifyTag(String modifyTag) {
            this.modifyTag = modifyTag;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }
    }
}