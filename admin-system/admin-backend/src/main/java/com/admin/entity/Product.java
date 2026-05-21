package com.admin.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Product {

    private Long id;
    private String name;
    private Long categoryId;
    private String mainImage;
    private String images;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stock;
    private Integer sales;
    private Integer status;
    private Integer sort;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private String categoryName;
    private List<ProductSku> skuList;
}
