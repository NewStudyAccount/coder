package com.admin.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Role {

    private Long id;
    private String roleName;
    private String roleKey;
    private Integer sort;
    private Integer status;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private List<Long> menuIds;
}
