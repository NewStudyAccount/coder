package com.example.demo.otc;

import java.time.LocalDateTime;

public class OcOrderRelationUu {
    private Long orderId;
    private Long orderLineId;
    private Long userIdA;
    private String serialNumberA;
    private String roleIdA;
    private String relationTypeCode;
    private Long userIdB;
    private String serialNumberB;
    private String roleIdB;
    private Integer callSequence; // order_no
    private String isPrimaryNumber;
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

    public Long getUserIdA() {
        return userIdA;
    }

    public void setUserIdA(Long userIdA) {
        this.userIdA = userIdA;
    }

    public String getSerialNumberA() {
        return serialNumberA;
    }

    public void setSerialNumberA(String serialNumberA) {
        this.serialNumberA = serialNumberA;
    }

    public String getRoleIdA() {
        return roleIdA;
    }

    public void setRoleIdA(String roleIdA) {
        this.roleIdA = roleIdA;
    }

    public String getRelationTypeCode() {
        return relationTypeCode;
    }

    public void setRelationTypeCode(String relationTypeCode) {
        this.relationTypeCode = relationTypeCode;
    }

    public Long getUserIdB() {
        return userIdB;
    }

    public void setUserIdB(Long userIdB) {
        this.userIdB = userIdB;
    }

    public String getSerialNumberB() {
        return serialNumberB;
    }

    public void setSerialNumberB(String serialNumberB) {
        this.serialNumberB = serialNumberB;
    }

    public String getRoleIdB() {
        return roleIdB;
    }

    public void setRoleIdB(String roleIdB) {
        this.roleIdB = roleIdB;
    }

    public Integer getCallSequence() {
        return callSequence;
    }

    public void setCallSequence(Integer callSequence) {
        this.callSequence = callSequence;
    }

    public String getIsPrimaryNumber() {
        return isPrimaryNumber;
    }

    public void setIsPrimaryNumber(String isPrimaryNumber) {
        this.isPrimaryNumber = isPrimaryNumber;
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
