package com.example.demo.otc;

import java.util.List;

public class MemberRelationSupplementResponse {
    private boolean success;
    private String message;
    private List<InstallLedgerDto> installLedgers;
    private List<PayRelationLedgerDto> payRelationLedgers;

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

    public List<InstallLedgerDto> getInstallLedgers() {
        return installLedgers;
    }

    public void setInstallLedgers(List<InstallLedgerDto> installLedgers) {
        this.installLedgers = installLedgers;
    }

    public List<PayRelationLedgerDto> getPayRelationLedgers() {
        return payRelationLedgers;
    }

    public void setPayRelationLedgers(List<PayRelationLedgerDto> payRelationLedgers) {
        this.payRelationLedgers = payRelationLedgers;
    }

    // 装机地址台账
    public static class InstallLedgerDto {
        private Long orderId;
        private Long orderLineId;
        private String installId;
        private String installItemId;
        private String userId;
        private String address;
        private String modifyTag;
        private String startDate;
        private String endDate;

        public Long getOrderId() { return orderId; }
        public void setOrderId(Long orderId) { this.orderId = orderId; }
        public Long getOrderLineId() { return orderLineId; }
        public void setOrderLineId(Long orderLineId) { this.orderLineId = orderLineId; }
        public String getInstallId() { return installId; }
        public void setInstallId(String installId) { this.installId = installId; }
        public String getInstallItemId() { return installItemId; }
        public void setInstallItemId(String installItemId) { this.installItemId = installItemId; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getModifyTag() { return modifyTag; }
        public void setModifyTag(String modifyTag) { this.modifyTag = modifyTag; }
        public String getStartDate() { return startDate; }
        public void setStartDate(String startDate) { this.startDate = startDate; }
        public String getEndDate() { return endDate; }
        public void setEndDate(String endDate) { this.endDate = endDate; }
    }

    // 付费关系台账
    public static class PayRelationLedgerDto {
        private Long orderId;
        private Long orderLineId;
        private String payRelationId;
        private String userId;
        private String accountId;
        private String chargeCategory;
        private String modifyTag;
        private String startDate;
        private String endDate;

        public Long getOrderId() { return orderId; }
        public void setOrderId(Long orderId) { this.orderId = orderId; }
        public Long getOrderLineId() { return orderLineId; }
        public void setOrderLineId(Long orderLineId) { this.orderLineId = orderLineId; }
        public String getPayRelationId() { return payRelationId; }
        public void setPayRelationId(String payRelationId) { this.payRelationId = payRelationId; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getAccountId() { return accountId; }
        public void setAccountId(String accountId) { this.accountId = accountId; }
        public String getChargeCategory() { return chargeCategory; }
        public void setChargeCategory(String chargeCategory) { this.chargeCategory = chargeCategory; }
        public String getModifyTag() { return modifyTag; }
        public void setModifyTag(String modifyTag) { this.modifyTag = modifyTag; }
        public String getStartDate() { return startDate; }
        public void setStartDate(String startDate) { this.startDate = startDate; }
        public String getEndDate() { return endDate; }
        public void setEndDate(String endDate) { this.endDate = endDate; }
    }
}