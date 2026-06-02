package com.admin.dto;

import lombok.Data;

@Data
public class LoginResponse {

    private String accessToken;
    private String refreshToken;
    private Long expiresIn;

    public LoginResponse(String accessToken, String refreshToken, Long expiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
    }
}
