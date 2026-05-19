package com.admin.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Dept {

    private Long id;
    private String deptName;
    private Long parentId;
    private Integer sort;
    private String leader;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private List<Dept> children;
}
