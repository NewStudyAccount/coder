package com.admin.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Menu {

    private Long id;
    private String menuName;
    private Long parentId;
    private String path;
    private String component;
    private String icon;
    private Integer sort;
    private Integer menuType;
    private String perms;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private List<Menu> children;
}
