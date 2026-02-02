package com.example.demo.entity;

import java.io.Serializable;

/**
 * 订单元素属性实体
 */
public class OcOrderElementItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private String orderId;
    private String orderLineId;
    private String elementId;
    private String elementItemId;
    private String elementTypeCode;
    private String attrCode;
    private String attrValue;
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

    public String getAttrCode() {
        return attrCode;
    }

    public void setAttrCode(String attrCode) {
        this.attrCode = attrCode;
    }

    public String getAttrValue() {
        return attrValue;
    }

    public void setAttrValue(String attrValue) {
        this.attrValue = attrValue;
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
