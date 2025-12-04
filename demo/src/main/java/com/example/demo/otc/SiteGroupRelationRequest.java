package com.example.demo.otc;

public class SiteGroupRelationRequest {
    private Long orderId;
    private Long orderLineId;
    private String snUserId;
    private String serialNumber;
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

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getSrd() {
        return srd;
    }

    public void setSrd(String srd) {
        this.srd = srd;
    }
}