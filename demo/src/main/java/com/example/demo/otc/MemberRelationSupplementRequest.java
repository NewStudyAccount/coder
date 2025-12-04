package com.example.demo.otc;

public class MemberRelationSupplementRequest {
    private Long orderId;
    private Long orderLineId;
    private String snUserId;
    private String parentSerialNumber;
    private String srd;

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

    public String getSnUserId() {
        return snUserId;
    }

    public void setSnUserId(String snUserId) {
        this.snUserId = snUserId;
    }

    public String getParentSerialNumber() {
        return parentSerialNumber;
    }

    public void setParentSerialNumber(String parentSerialNumber) {
        this.parentSerialNumber = parentSerialNumber;
    }

    public String getSrd() {
        return srd;
    }

    public void setSrd(String srd) {
        this.srd = srd;
    }
}