package com.admin.service;

import com.admin.entity.Oauth2User;
import com.admin.entity.User;
import com.admin.mapper.Oauth2UserMapper;
import com.admin.mapper.UserMapper;
import com.admin.oauth2.Oauth2Provider;
import com.admin.oauth2.Oauth2ProviderRegistry;
import com.admin.oauth2.Oauth2UserInfo;
import com.admin.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class Oauth2Service {

    @Autowired
    private Oauth2ProviderRegistry registry;

    @Autowired
    private Oauth2UserMapper oauth2UserMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String getAuthorizeUrl(String provider, String state) {
        return registry.getProvider(provider).buildAuthorizeUrl(state);
    }

    @Transactional
    public Map<String, Object> handleCallback(String provider, String code) {
        Oauth2Provider oauth2Provider = registry.getProvider(provider);
        String accessToken = oauth2Provider.exchangeToken(code);
        Oauth2UserInfo oauth2UserInfo = oauth2Provider.fetchUserInfo(accessToken);

        String providerId = oauth2UserInfo.getId();
        String providerUsername = oauth2UserInfo.getUsername();
        String avatarUrl = oauth2UserInfo.getAvatarUrl();
        String email = oauth2UserInfo.getEmail();

        Oauth2User oauth2User = oauth2UserMapper.selectByProviderAndProviderId(provider, providerId);
        User user;

        if (oauth2User != null) {
            user = userMapper.selectById(oauth2User.getUserId());
            if (user == null) {
                throw new RuntimeException("关联用户不存在");
            }
        } else {
            String username = provider + "_" + providerId;
            User existUser = userMapper.selectByUsername(username);
            if (existUser != null) {
                user = existUser;
            } else {
                user = new User();
                user.setUsername(username);
                user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                user.setNickname(providerUsername != null ? providerUsername : username);
                user.setEmail(email);
                user.setAvatar(avatarUrl);
                user.setStatus(1);
                userMapper.insert(user);
                userMapper.insertUserRole(user.getId(), 2L);
            }

            Oauth2User newOauth2User = new Oauth2User();
            newOauth2User.setUserId(user.getId());
            newOauth2User.setProvider(provider);
            newOauth2User.setProviderId(providerId);
            newOauth2User.setProviderUsername(providerUsername);
            newOauth2User.setAvatarUrl(avatarUrl);
            newOauth2User.setEmail(email);
            oauth2UserMapper.insert(newOauth2User);
        }

        String role = user.getRoleKey() != null ? user.getRoleKey() : "user";
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), role);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);

        Map<String, Object> userInfoMap = new HashMap<>();
        userInfoMap.put("id", user.getId());
        userInfoMap.put("username", user.getUsername());
        userInfoMap.put("nickname", user.getNickname());
        userInfoMap.put("avatar", user.getAvatar() != null ? user.getAvatar() : avatarUrl);
        userInfoMap.put("role", role);
        userInfoMap.put("roleName", user.getRoleName());
        result.put("userInfo", userInfoMap);

        return result;
    }

    public List<Map<String, String>> getAvailableProviders() {
        return registry.getAvailableProviders();
    }

    public String getFrontendRedirectUrl() {
        return registry.getFrontendRedirectUrl();
    }
}
