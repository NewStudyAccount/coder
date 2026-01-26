package com.example.demo.otc;

import java.time.LocalDateTime;

public class OcOrderElement {
    private Long orderId;
    private Long orderLineId;
    private Long elementItemId; // 主键
    private Long prodItemId;
    private String elementId;
    private String elementType;
    private String modifyTag;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

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

    public Long getElementItemId() {
        return elementItemId;
    }

    public void setElementItemId(Long elementItemId) {
        this.elementItemId = elementItemId;
    }

    public Long getProdItemId() {
        return prodItemId;
    }

    public void setProdItemId(Long prodItemId) {
        this.prodItemId = prodItemId;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public String getElementType() {
        return elementType;
    }

    public void setElementType(String elementType) {
        this.elementType = elementType;
    }

    public String getModifyTag() {
        return modifyTag;
    }

    public void setModifyTag(String modifyTag) {
        this.modifyTag = modifyTag;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
}
