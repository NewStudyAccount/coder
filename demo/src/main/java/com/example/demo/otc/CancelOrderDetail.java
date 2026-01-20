package com.example.demo.otc;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CancelOrderDetail {
    private String serialNumber;
    private Long orderId;
    private Long orderLineId;
    private String tradeTypeCode;
    private LocalDateTime srd;

    // Getters and Setters
    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

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

    public LocalDateTime getSrd() {
        return srd;
    }

    public void setSrd(LocalDateTime srd) {
        this.srd = srd;
    }

    @Override
    public String toString() {
        return "CancelOrderDetail{" +
                "serialNumber='" + serialNumber + '\'' +
                ", orderId=" + orderId +
                ", orderLineId=" + orderLineId +
                ", tradeTypeCode='" + tradeTypeCode + '\'' +
                ", srd=" + srd +
                '}';
    }
}
