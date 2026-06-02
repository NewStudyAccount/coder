package com.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("sys_role_dept")
public class RoleDept {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long roleId;
    private Long deptId;
}
