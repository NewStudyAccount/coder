package com.example.demo.otc;

public class OcOrderLine {
    private Long orderId;
    private Long orderLineId;
    private String serialNumber;
    private Long snCustId;
    private Long snUserId;
    private String tradeTypeCode;
    private String cancelTag;
    private Integer lineLevel;
    private Integer produceOrderState;
    private java.time.LocalDateTime srd;
    private String netTypeCode;
    private String sceneType;
    private String parentSerialNumber;
    private Long custId;
    
    // Added fields
    private Long userId;
    private String mainProductId;
    private String mainProductName;
    private String mainProductType;
    private String productFamily;
    
    // Additional fields for ChangePrimaryNumber
    private String departId;
    private String channelId;
    private String channelType;
    private String cityCode;
    private String eparchyCode;
    private String provinceCode;
    private java.time.LocalDateTime acceptDate;
    private java.time.LocalDateTime updateTime;
    private String inModeCode;

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

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Long getSnCustId() {
        return snCustId;
    }

    public void setSnCustId(Long snCustId) {
        this.snCustId = snCustId;
    }

    public Long getSnUserId() {
        return snUserId;
    }

    public void setSnUserId(Long snUserId) {
        this.snUserId = snUserId;
    }

    public String getTradeTypeCode() {
        return tradeTypeCode;
    }

    public void setTradeTypeCode(String tradeTypeCode) {
        this.tradeTypeCode = tradeTypeCode;
    }

    public String getCancelTag() {
        return cancelTag;
    }

    public void setCancelTag(String cancelTag) {
        this.cancelTag = cancelTag;
    }

    public Integer getLineLevel() {
        return lineLevel;
    }

    public void setLineLevel(Integer lineLevel) {
        this.lineLevel = lineLevel;
    }

    public Integer getProduceOrderState() {
        return produceOrderState;
    }

    public void setProduceOrderState(Integer produceOrderState) {
        this.produceOrderState = produceOrderState;
    }

    public java.time.LocalDateTime getSrd() {
        return srd;
    }

    public void setSrd(java.time.LocalDateTime srd) {
        this.srd = srd;
    }

    public String getNetTypeCode() {
        return netTypeCode;
    }

    public void setNetTypeCode(String netTypeCode) {
        this.netTypeCode = netTypeCode;
    }

    public String getSceneType() {
        return sceneType;
    }

    public void setSceneType(String sceneType) {
        this.sceneType = sceneType;
    }

    public String getParentSerialNumber() {
        return parentSerialNumber;
    }

    public void setParentSerialNumber(String parentSerialNumber) {
        this.parentSerialNumber = parentSerialNumber;
    }

    public Long getCustId() {
        return custId;
    }

    public void setCustId(Long custId) {
        this.custId = custId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getMainProductId() {
        return mainProductId;
    }

    public void setMainProductId(String mainProductId) {
        this.mainProductId = mainProductId;
    }

    public String getMainProductName() {
        return mainProductName;
    }

    public void setMainProductName(String mainProductName) {
        this.mainProductName = mainProductName;
    }

    public String getMainProductType() {
        return mainProductType;
    }

    public void setMainProductType(String mainProductType) {
        this.mainProductType = mainProductType;
    }

    public String getProductFamily() {
        return productFamily;
    }

    public void setProductFamily(String productFamily) {
        this.productFamily = productFamily;
    }

    public String getDepartId() {
        return departId;
    }

    public void setDepartId(String departId) {
        this.departId = departId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getEparchyCode() {
        return eparchyCode;
    }

    public void setEparchyCode(String eparchyCode) {
        this.eparchyCode = eparchyCode;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public java.time.LocalDateTime getAcceptDate() {
        return acceptDate;
    }

    public void setAcceptDate(java.time.LocalDateTime acceptDate) {
        this.acceptDate = acceptDate;
    }

    public java.time.LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(java.time.LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public String getInModeCode() {
        return inModeCode;
    }

    public void setInModeCode(String inModeCode) {
        this.inModeCode = inModeCode;
    }
}
