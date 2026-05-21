package com.admin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Member {

    private Long id;
    private String username;

    @JsonIgnore
    private String password;

    private String nickname;
    private String phone;
    private String email;
    private String avatar;
    private Integer level;
    private Integer points;
    private BigDecimal balance;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
