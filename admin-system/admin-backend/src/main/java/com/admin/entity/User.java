package com.admin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {

    private Long id;
    private String username;

    @JsonIgnore
    private String password;

    private String nickname;
    private String email;
    private String phone;
    private String avatar;
    private Long deptId;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private String deptName;
    private String roleKey;
    private String roleName;
}
