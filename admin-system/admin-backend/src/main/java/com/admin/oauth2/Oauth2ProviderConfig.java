package com.admin.oauth2;

import lombok.Data;

@Data
public class Oauth2ProviderConfig {

    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String authorizeUrl;
    private String tokenUrl;
    private String userInfoUrl;
    private String scope;
    private UserInfoMapping userInfoMapping;

    @Data
    public static class UserInfoMapping {
        private String idField;
        private String usernameField;
        private String avatarField;
        private String emailField;
    }
}
