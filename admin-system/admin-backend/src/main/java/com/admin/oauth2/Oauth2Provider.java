package com.admin.oauth2;

public interface Oauth2Provider {

    String getName();

    String buildAuthorizeUrl(String state);

    String exchangeToken(String code);

    Oauth2UserInfo fetchUserInfo(String accessToken);
}
