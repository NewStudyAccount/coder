package com.example.demo.otc;

public class OcOrderLine {
    private Long orderId;
    private Long orderLineId;
    private String serialNumber;
    private Long snCustId;

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

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Long getSnCustId() {
        return snCustId;
    }

    public void setSnCustId(Long snCustId) {
        this.snCustId = snCustId;
    }
}
