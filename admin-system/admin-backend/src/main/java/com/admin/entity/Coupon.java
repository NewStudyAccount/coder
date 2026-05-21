package com.admin.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Coupon {

    private Long id;
    private String name;
    private Integer type;
    private BigDecimal discount;
    private BigDecimal minAmount;
    private Integer totalCount;
    private Integer remainCount;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
