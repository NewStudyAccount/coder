package com.admin.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class MenuCreateRequest {

    @NotBlank(message = "菜单名称不能为空")
    @Size(max = 50, message = "菜单名称最多50个字符")
    private String menuName;

    @NotNull(message = "父菜单ID不能为空")
    private Long parentId;

    private Integer sort;
    private String path;
    private String component;
    private String queryParam;
    private Integer isFrame;
    private Integer isCache;
    @NotNull(message = "菜单类型不能为空")
    private Integer menuType;
    private Integer visible;
    private Integer status;
    private String perms;
    private String icon;
    private String remark;
}
