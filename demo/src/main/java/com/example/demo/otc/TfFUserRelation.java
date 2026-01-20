package com.example.demo.otc;

import java.time.LocalDateTime;

public class TfFUserRelation {
    private Long userIdA;
    private Long userIdB;
    private String serialNumberA;
    private String serialNumberB;
    private String relationTypeCode;
    private String roleCodeA;
    private String roleCodeB;
    private Integer orderNo; // call_sequence?
    private String shortCode;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public Long getUserIdA() {
        return userIdA;
    }

    public void setUserIdA(Long userIdA) {
        this.userIdA = userIdA;
    }

    public Long getUserIdB() {
        return userIdB;
    }

    public void setUserIdB(Long userIdB) {
        this.userIdB = userIdB;
    }

    public String getSerialNumberA() {
        return serialNumberA;
    }

    public void setSerialNumberA(String serialNumberA) {
        this.serialNumberA = serialNumberA;
    }

    public String getSerialNumberB() {
        return serialNumberB;
    }

    public void setSerialNumberB(String serialNumberB) {
        this.serialNumberB = serialNumberB;
    }

    public String getRelationTypeCode() {
        return relationTypeCode;
    }

    public void setRelationTypeCode(String relationTypeCode) {
        this.relationTypeCode = relationTypeCode;
    }

    public String getRoleCodeA() {
        return roleCodeA;
    }

    public void setRoleCodeA(String roleCodeA) {
        this.roleCodeA = roleCodeA;
    }

    public String getRoleCodeB() {
        return roleCodeB;
    }

    public void setRoleCodeB(String roleCodeB) {
        this.roleCodeB = roleCodeB;
    }

    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
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
