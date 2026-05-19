package com.admin.oauth2.provider;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.admin.oauth2.AbstractOauth2Provider;
import com.admin.oauth2.Oauth2ProviderConfig;
import com.admin.oauth2.Oauth2UserInfo;

public class FeishuProvider extends AbstractOauth2Provider {

    public FeishuProvider(Oauth2ProviderConfig config) {
        super(config);
    }

    @Override
    public String getName() {
        return "feishu";
    }

    @Override
    public String buildAuthorizeUrl(String state) {
        return config.getAuthorizeUrl()
                + "?app_id=" + config.getClientId()
                + "&redirect_uri=" + urlEncode(config.getRedirectUri())
                + "&response_type=code"
                + "&state=" + state;
    }

    @Override
    public String exchangeToken(String code) {
        try {
            JSONObject body = new JSONObject();
            body.set("app_id", config.getClientId());
            body.set("app_secret", config.getClientSecret());
            body.set("grant_type", "authorization_code");
            body.set("code", code);

            String response = httpPost(config.getTokenUrl(), body.toString(), "application/json; charset=utf-8");
            JSONObject json = JSONUtil.parseObj(response);

            if (json.getInt("code") != 0) {
                throw new RuntimeException("飞书令牌交换失败: " + json.getStr("msg"));
            }

            JSONObject data = json.getByPath("data", JSONObject.class);
            if (data == null) {
                throw new RuntimeException("飞书令牌交换响应格式异常");
            }

            String accessToken = data.getStr("access_token");
            return accessToken;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("飞书令牌交换异常: " + e.getMessage());
        }
    }

    @Override
    public Oauth2UserInfo fetchUserInfo(String accessToken) {
        try {
            String response = httpGet(config.getUserInfoUrl(),
                    "Authorization", "Bearer " + accessToken,
                    "Content-Type", "application/json; charset=utf-8");

            JSONObject json = JSONUtil.parseObj(response);
            if (json.getInt("code") != 0) {
                throw new RuntimeException("获取飞书用户信息失败: " + json.getStr("msg"));
            }

            JSONObject data = json.getByPath("data", JSONObject.class);
            if (data == null) {
                data = json;
            }

            Oauth2UserInfo userInfo = new Oauth2UserInfo();
            Oauth2ProviderConfig.UserInfoMapping mapping = config.getUserInfoMapping();
            if (mapping != null) {
                userInfo.setId(getFieldValue(data, mapping.getIdField()));
                userInfo.setUsername(getFieldValue(data, mapping.getUsernameField()));
                userInfo.setAvatarUrl(getFieldValue(data, mapping.getAvatarField()));
                userInfo.setEmail(getFieldValue(data, mapping.getEmailField()));
            }
            return userInfo;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("获取飞书用户信息异常: " + e.getMessage());
        }
    }
}
