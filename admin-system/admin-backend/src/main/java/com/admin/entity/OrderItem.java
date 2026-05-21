package com.admin.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItem {

    private Long id;
    private Long orderId;
    private Long productId;
    private Long skuId;
    private String productName;
    private String skuName;
    private String image;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal totalPrice;
}
