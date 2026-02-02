package com.example.demo.entity;

import java.io.Serializable;

/**
 * 订单产品元素实体
 */
public class OcOrderProductElement implements Serializable {
    private static final long serialVersionUID = 1L;

    private String orderId;
    private String orderLineId;
    private String productId;
    private String prodItemId;
    private String elementId;
    private String elementItemId;
    private String elementTypeCode;
    private String isMainElement;
    private String modifyTag;
    private String startDate;
    private String endDate;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderLineId() {
        return orderLineId;
    }

    public void setOrderLineId(String orderLineId) {
        this.orderLineId = orderLineId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProdItemId() {
        return prodItemId;
    }

    public void setProdItemId(String prodItemId) {
        this.prodItemId = prodItemId;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public String getElementItemId() {
        return elementItemId;
    }

    public void setElementItemId(String elementItemId) {
        this.elementItemId = elementItemId;
    }

    public String getElementTypeCode() {
        return elementTypeCode;
    }

    public void setElementTypeCode(String elementTypeCode) {
        this.elementTypeCode = elementTypeCode;
    }

    public String getIsMainElement() {
        return isMainElement;
    }

    public void setIsMainElement(String isMainElement) {
        this.isMainElement = isMainElement;
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
