package com.example.demo.otc;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class MemberRelationSupplementRequest {
    @NotNull(message = "orderId不能为空")
    private Long orderId;
    @NotNull(message = "orderLineId不能为空")
    private Long orderLineId;
    @NotBlank(message = "snUserId不能为空")
    private String snUserId;
    private String parentSerialNumber;
    @NotBlank(message = "srd不能为空")
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