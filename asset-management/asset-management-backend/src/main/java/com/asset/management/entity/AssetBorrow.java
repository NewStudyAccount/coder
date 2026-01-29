package com.asset.management.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 资产借用记录实体类
 */
@Data
public class AssetBorrow {
    
    private Long id;
    private Long assetId;
    private String assetCode;  // 关联查询时使用
    private String assetName;  // 关联查询时使用
    private Long borrowerId;
    private String borrowerName;
    private LocalDateTime borrowDate;
    private LocalDateTime expectedReturnDate;
    private LocalDateTime actualReturnDate;
    private String borrowReason;
    private String returnStatus;
    private String returnCondition;
    private String remarks;
    private String approver;
    private LocalDateTime approveTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
