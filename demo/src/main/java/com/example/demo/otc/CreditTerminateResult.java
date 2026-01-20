package com.example.demo.otc;

import java.util.List;

public class CreditTerminateResult {
    private List<CancelOrderDetail> cancelOrderDetailList;
    private List<Long> notProcessSNList;
    private String message;

    public CreditTerminateResult(List<CancelOrderDetail> cancelOrderDetailList, List<Long> notProcessSNList, String message) {
        this.cancelOrderDetailList = cancelOrderDetailList;
        this.notProcessSNList = notProcessSNList;
        this.message = message;
    }

    public List<CancelOrderDetail> getCancelOrderDetailList() {
        return cancelOrderDetailList;
    }

    public void setCancelOrderDetailList(List<CancelOrderDetail> cancelOrderDetailList) {
        this.cancelOrderDetailList = cancelOrderDetailList;
    }

    public List<Long> getNotProcessSNList() {
        return notProcessSNList;
    }

    public void setNotProcessSNList(List<Long> notProcessSNList) {
        this.notProcessSNList = notProcessSNList;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
