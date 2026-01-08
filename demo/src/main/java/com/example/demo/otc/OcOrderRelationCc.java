package com.example.demo.otc;

import java.time.LocalDateTime;

public class OcOrderRelationCc {
    private Long custIdA;
    private Long custIdB;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String modifyTag;

    public Long getCustIdA() {
        return custIdA;
    }

    public void setCustIdA(Long custIdA) {
        this.custIdA = custIdA;
    }

    public Long getCustIdB() {
        return custIdB;
    }

    public void setCustIdB(Long custIdB) {
        this.custIdB = custIdB;
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

    public String getModifyTag() {
        return modifyTag;
    }

    public void setModifyTag(String modifyTag) {
        this.modifyTag = modifyTag;
    }
}
