package com.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long deptId;
    private String username;
    private String nickname;
    private String password;
    private String email;
    private String phone;
    private Integer gender;
    private String avatar;
    private Integer status;
    @TableLogic
    private Integer delFlag;
    private String loginIp;
    private LocalDateTime loginDate;
    private String createBy;
    private LocalDateTime createTime;
    private String updateBy;
    private LocalDateTime updateTime;
    private String remark;
}
