package com.admin.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Category {

    private Long id;
    private String name;
    private Long parentId;
    private String icon;
    private Integer sort;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private List<Category> children;
}
