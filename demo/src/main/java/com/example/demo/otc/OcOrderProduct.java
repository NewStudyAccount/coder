package com.example.demo.otc;

import java.time.LocalDateTime;

public class OcOrderProduct {
    private Long orderId;
    private Long orderLineId;
    private Long prodItemId; // 主键
    private String productId;
    private String productTypeCode;
    private String productMode;
    private LocalDateTime activeDate;
    private LocalDateTime terminateDate;
    private Long userId;

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

    public Long getProdItemId() {
        return prodItemId;
    }

    public void setProdItemId(Long prodItemId) {
        this.prodItemId = prodItemId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductTypeCode() {
        return productTypeCode;
    }

    public void setProductTypeCode(String productTypeCode) {
        this.productTypeCode = productTypeCode;
    }

    public String getProductMode() {
        return productMode;
    }

    public void setProductMode(String productMode) {
        this.productMode = productMode;
    }

    public LocalDateTime getActiveDate() {
        return activeDate;
    }

    public void setActiveDate(LocalDateTime activeDate) {
        this.activeDate = activeDate;
    }

    public LocalDateTime getTerminateDate() {
        return terminateDate;
    }

    public void setTerminateDate(LocalDateTime terminateDate) {
        this.terminateDate = terminateDate;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
