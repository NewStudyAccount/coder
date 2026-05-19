package com.admin.oauth2;

import lombok.Data;

@Data
public class Oauth2UserInfo {

    private String id;
    private String username;
    private String avatarUrl;
    private String email;
}
