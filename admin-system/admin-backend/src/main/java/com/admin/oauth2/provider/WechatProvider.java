package com.admin.oauth2.provider;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.admin.oauth2.AbstractOauth2Provider;
import com.admin.oauth2.Oauth2ProviderConfig;
import com.admin.oauth2.Oauth2UserInfo;

public class WechatProvider extends AbstractOauth2Provider {

    public WechatProvider(Oauth2ProviderConfig config) {
        super(config);
    }

    @Override
    public String getName() {
        return "wechat";
    }

    @Override
    public String buildAuthorizeUrl(String state) {
        return config.getAuthorizeUrl()
                + "?appid=" + config.getClientId()
                + "&redirect_uri=" + urlEncode(config.getRedirectUri())
                + "&response_type=code"
                + "&scope=" + urlEncode(config.getScope())
                + "&state=" + state
                + "#wechat_redirect";
    }

    @Override
    public String exchangeToken(String code) {
        try {
            String url = config.getTokenUrl()
                    + "?appid=" + urlEncode(config.getClientId())
                    + "&secret=" + urlEncode(config.getClientSecret())
                    + "&code=" + urlEncode(code)
                    + "&grant_type=authorization_code";

            String response = httpGet(url);
            JSONObject json = JSONUtil.parseObj(response);

            if (json.containsKey("errcode") && json.getInt("errcode") != 0) {
                throw new RuntimeException("微信令牌交换失败: " + json.getStr("errmsg"));
            }

            String accessToken = json.getStr("access_token");
            String openid = json.getStr("openid");
            return accessToken + "|" + openid;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("微信令牌交换异常: " + e.getMessage());
        }
    }

    @Override
    public Oauth2UserInfo fetchUserInfo(String accessTokenWithOpenid) {
        try {
            String[] parts = accessTokenWithOpenid.split("\\|");
            String accessToken = parts[0];
            String openid = parts.length > 1 ? parts[1] : "";

            String url = config.getUserInfoUrl()
                    + "?access_token=" + urlEncode(accessToken)
                    + "&openid=" + urlEncode(openid);

            String response = httpGet(url);
            JSONObject json = JSONUtil.parseObj(response);

            if (json.containsKey("errcode") && json.getInt("errcode") != 0) {
                throw new RuntimeException("获取微信用户信息失败: " + json.getStr("errmsg"));
            }

            Oauth2UserInfo userInfo = new Oauth2UserInfo();
            Oauth2ProviderConfig.UserInfoMapping mapping = config.getUserInfoMapping();
            if (mapping != null) {
                if (mapping.getIdField() != null && !mapping.getIdField().isEmpty()) {
                    String idVal = getFieldValue(json, mapping.getIdField());
                    userInfo.setId(idVal != null ? idVal : openid);
                } else {
                    userInfo.setId(openid);
                }
                userInfo.setUsername(getFieldValue(json, mapping.getUsernameField()));
                userInfo.setAvatarUrl(getFieldValue(json, mapping.getAvatarField()));
                userInfo.setEmail(getFieldValue(json, mapping.getEmailField()));
            }
            return userInfo;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("获取微信用户信息异常: " + e.getMessage());
        }
    }
}
