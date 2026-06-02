package com.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("sys_role_menu")
public class RoleMenu {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long roleId;
    private Long menuId;
}
