package com.admin.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Oauth2User {

    private Long id;
    private Long userId;
    private String provider;
    private String providerId;
    private String providerUsername;
    private String avatarUrl;
    private String email;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
