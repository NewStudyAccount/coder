package com.admin.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Order {

    private Long id;
    private String orderNo;
    private Long memberId;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private LocalDateTime payTime;
    private Integer payType;
    private Integer status;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private String memberName;
    private List<OrderItem> items;
}
