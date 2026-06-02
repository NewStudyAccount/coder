package com.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_dept")
public class Dept {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long parentId;
    private String ancestors;
    private String deptName;
    private Integer sort;
    private String leader;
    private String phone;
    private String email;
    private Integer status;
    private String createBy;
    private LocalDateTime createTime;
    private String updateBy;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer delFlag;

    @TableField(exist = false)
    private java.util.List<Dept> children;
}
