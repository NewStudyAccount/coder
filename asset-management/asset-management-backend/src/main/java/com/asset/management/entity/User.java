package com.asset.management.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
public class User {
    
    private Long id;
    private String username;
    
    @JsonIgnore  // 返回给前端时忽略密码字段
    private String password;
    
    private String realName;
    private String email;
    private String phone;
    private String department;
    private String role;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
