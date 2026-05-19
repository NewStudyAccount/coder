package com.admin.controller;

import com.admin.common.Result;
import com.admin.service.Oauth2Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/oauth2")
public class Oauth2Controller {

    @Autowired
    private Oauth2Service oauth2Service;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/providers")
    public Result<List<Map<String, String>>> providers() {
        return Result.success(oauth2Service.getAvailableProviders());
    }

    @GetMapping("/authorize/{provider}")
    public Result<Map<String, String>> authorize(@PathVariable String provider) {
        String state = UUID.randomUUID().toString().replace("-", "");
        String authorizeUrl = oauth2Service.getAuthorizeUrl(provider, state);

        Map<String, String> result = new HashMap<>();
        result.put("authorizeUrl", authorizeUrl);
        result.put("state", state);
        return Result.success(result);
    }

    @GetMapping("/callback/{provider}")
    public void callback(@PathVariable String provider,
                         @RequestParam("code") String code,
                         @RequestParam(value = "state", required = false) String state,
                         HttpServletResponse response) throws IOException {
        try {
            Map<String, Object> loginResult = oauth2Service.handleCallback(provider, code);
            String token = (String) loginResult.get("token");
            String userInfoJson = objectMapper.writeValueAsString(loginResult.get("userInfo"));
            String encodedUserInfo = Base64.getUrlEncoder().withoutPadding().encodeToString(userInfoJson.getBytes());

            String redirectUrl = oauth2Service.getFrontendRedirectUrl()
                    + "?token=" + token
                    + "&userInfo=" + encodedUserInfo;
            response.sendRedirect(redirectUrl);
        } catch (Exception e) {
            String encodedError = Base64.getUrlEncoder().withoutPadding().encodeToString(e.getMessage().getBytes());
            String errorUrl = oauth2Service.getFrontendRedirectUrl() + "?error=" + encodedError;
            response.sendRedirect(errorUrl);
        }
    }
}
