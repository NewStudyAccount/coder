package com.admin.oauth2.provider;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.admin.oauth2.AbstractOauth2Provider;
import com.admin.oauth2.Oauth2ProviderConfig;
import com.admin.oauth2.Oauth2UserInfo;

public class DingtalkProvider extends AbstractOauth2Provider {

    public DingtalkProvider(Oauth2ProviderConfig config) {
        super(config);
    }

    @Override
    public String getName() {
        return "dingtalk";
    }

    @Override
    public String buildAuthorizeUrl(String state) {
        return config.getAuthorizeUrl()
                + "?appid=" + config.getClientId()
                + "&response_type=code"
                + "&scope=" + urlEncode(config.getScope())
                + "&state=" + state
                + "&redirect_uri=" + urlEncode(config.getRedirectUri());
    }

    @Override
    public String exchangeToken(String code) {
        try {
            String url = config.getTokenUrl()
                    + "?appid=" + urlEncode(config.getClientId())
                    + "&appsecret=" + urlEncode(config.getClientSecret());

            String response = httpGet(url);
            JSONObject json = JSONUtil.parseObj(response);

            if (json.getInt("errcode") != 0) {
                throw new RuntimeException("钉钉获取access_token失败: " + json.getStr("errmsg"));
            }

            String accessToken = json.getStr("access_token");
            return accessToken + "|" + code;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("钉钉令牌交换异常: " + e.getMessage());
        }
    }

    @Override
    public Oauth2UserInfo fetchUserInfo(String accessTokenWithCode) {
        try {
            String[] parts = accessTokenWithCode.split("\\|");
            String accessToken = parts[0];
            String code = parts.length > 1 ? parts[1] : "";

            String url = config.getUserInfoUrl()
                    + "?access_token=" + urlEncode(accessToken);

            String body = "{\"tmp_auth_code\":\"" + code + "\"}";
            String response = httpPost(url, body, "application/json");

            JSONObject json = JSONUtil.parseObj(response);
            if (json.getInt("errcode") != 0) {
                throw new RuntimeException("获取钉钉用户信息失败: " + json.getStr("errmsg"));
            }

            JSONObject userInfoJson = json.getByPath("user_info", JSONObject.class);
            if (userInfoJson == null) {
                userInfoJson = json;
            }

            Oauth2UserInfo userInfo = new Oauth2UserInfo();
            Oauth2ProviderConfig.UserInfoMapping mapping = config.getUserInfoMapping();
            if (mapping != null) {
                userInfo.setId(getFieldValue(userInfoJson, mapping.getIdField()));
                userInfo.setUsername(getFieldValue(userInfoJson, mapping.getUsernameField()));
                userInfo.setAvatarUrl(getFieldValue(userInfoJson, mapping.getAvatarField()));
                userInfo.setEmail(getFieldValue(userInfoJson, mapping.getEmailField()));
            }
            return userInfo;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("获取钉钉用户信息异常: " + e.getMessage());
        }
    }
}
