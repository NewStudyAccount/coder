package com.asset.management.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 资产实体类
 */
@Data
public class Asset {
    
    private Long id;
    private String assetCode;
    private String assetName;
    private Long categoryId;
    private String categoryName;  // 关联查询时使用
    private String specifications;
    private String unit;
    private LocalDate purchaseDate;
    private BigDecimal purchasePrice;
    private String supplier;
    private Integer warrantyPeriod;
    private String status;
    private String location;
    private String responsiblePerson;
    private String description;
    private String imageUrl;
    private String createBy;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
