package com.example.demo.otc;

public class TfFUserSvcState {
    private Long userId;
    private String serviceId;
    private String serviceStateCode;
    private String mainTag;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceStateCode() {
        return serviceStateCode;
    }

    public void setServiceStateCode(String serviceStateCode) {
        this.serviceStateCode = serviceStateCode;
    }

    public String getMainTag() {
        return mainTag;
    }

    public void setMainTag(String mainTag) {
        this.mainTag = mainTag;
    }
}
