package com.asset.management.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 资产分类实体类
 */
@Data
public class AssetCategory {
    
    private Long id;
    private String categoryName;
    private String categoryCode;
    private Long parentId;
    private String description;
    private Integer sortOrder;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
