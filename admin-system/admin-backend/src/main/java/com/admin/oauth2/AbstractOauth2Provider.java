package com.admin.oauth2;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public abstract class AbstractOauth2Provider implements Oauth2Provider {

    protected final Oauth2ProviderConfig config;

    protected AbstractOauth2Provider(Oauth2ProviderConfig config) {
        this.config = config;
    }

    @Override
    public String buildAuthorizeUrl(String state) {
        return config.getAuthorizeUrl()
                + "?client_id=" + config.getClientId()
                + "&redirect_uri=" + urlEncode(config.getRedirectUri())
                + "&scope=" + urlEncode(config.getScope())
                + "&response_type=code"
                + "&state=" + state;
    }

    @Override
    public String exchangeToken(String code) {
        try {
            String params = "client_id=" + urlEncode(config.getClientId())
                    + "&client_secret=" + urlEncode(config.getClientSecret())
                    + "&code=" + urlEncode(code)
                    + "&redirect_uri=" + urlEncode(config.getRedirectUri())
                    + "&grant_type=authorization_code";

            String response = httpPost(config.getTokenUrl(), params,
                    "application/x-www-form-urlencoded", "Accept", "application/json");
            JSONObject json = JSONUtil.parseObj(response);
            return json.getStr("access_token");
        } catch (Exception e) {
            throw new RuntimeException(getName() + "令牌交换异常: " + e.getMessage());
        }
    }

    @Override
    public Oauth2UserInfo fetchUserInfo(String accessToken) {
        try {
            String response = httpGet(config.getUserInfoUrl(),
                    "Authorization", "Bearer " + accessToken,
                    "Accept", "application/json");
            JSONObject json = JSONUtil.parseObj(response);

            Oauth2UserInfo userInfo = new Oauth2UserInfo();
            Oauth2ProviderConfig.UserInfoMapping mapping = config.getUserInfoMapping();
            if (mapping != null) {
                userInfo.setId(getFieldValue(json, mapping.getIdField()));
                userInfo.setUsername(getFieldValue(json, mapping.getUsernameField()));
                userInfo.setAvatarUrl(getFieldValue(json, mapping.getAvatarField()));
                userInfo.setEmail(getFieldValue(json, mapping.getEmailField()));
            }
            return userInfo;
        } catch (Exception e) {
            throw new RuntimeException("获取" + getName() + "用户信息异常: " + e.getMessage());
        }
    }

    protected String getFieldValue(JSONObject json, String field) {
        if (field == null || field.isEmpty()) {
            return null;
        }
        Object value = json.get(field);
        return value != null ? String.valueOf(value) : null;
    }

    protected String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (Exception e) {
            return value;
        }
    }

    protected String httpGet(String urlStr, String... headers) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);
        for (int i = 0; i + 1 < headers.length; i += 2) {
            conn.setRequestProperty(headers[i], headers[i + 1]);
        }

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("HTTP GET 请求失败，状态码: " + responseCode);
        }

        return readResponse(conn);
    }

    protected String httpPost(String urlStr, String body, String contentType, String... headers) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);
        conn.setRequestProperty("Content-Type", contentType);
        for (int i = 0; i + 1 < headers.length; i += 2) {
            conn.setRequestProperty(headers[i], headers[i + 1]);
        }

        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            String errorMsg = "";
            if (conn.getErrorStream() != null) {
                BufferedReader errorReader = new BufferedReader(
                        new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
                StringBuilder errorSb = new StringBuilder();
                String line;
                while ((line = errorReader.readLine()) != null) {
                    errorSb.append(line);
                }
                errorReader.close();
                errorMsg = errorSb.toString();
            }
            throw new RuntimeException("HTTP POST 请求失败，状态码: " + responseCode + "，响应: " + errorMsg);
        }

        return readResponse(conn);
    }

    protected String readResponse(HttpURLConnection conn) throws Exception {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        return sb.toString();
    }
}
