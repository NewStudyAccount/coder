package com.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_menu")
public class Menu {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String menuName;
    private Long parentId;
    private Integer sort;
    private String path;
    private String component;
    private String queryParam;
    private Integer isFrame;
    private Integer isCache;
    private Integer menuType;
    private Integer visible;
    private Integer status;
    private String perms;
    private String icon;
    private String createBy;
    private LocalDateTime createTime;
    private String updateBy;
    private LocalDateTime updateTime;
    private String remark;

    @TableField(exist = false)
    private java.util.List<Menu> children;
}
