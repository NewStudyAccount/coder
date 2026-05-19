package com.admin.oauth2;

import com.admin.oauth2.provider.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ConfigurationProperties(prefix = "oauth2")
public class Oauth2ProviderRegistry {

    private Map<String, Oauth2ProviderConfig> providers = new HashMap<>();
    private String frontendRedirectUrl;

    private final Map<String, Oauth2Provider> providerMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        if (providers != null) {
            for (Map.Entry<String, Oauth2ProviderConfig> entry : providers.entrySet()) {
                String name = entry.getKey();
                Oauth2ProviderConfig config = entry.getValue();
                Oauth2Provider provider = createProvider(name, config);
                if (provider != null) {
                    providerMap.put(name, provider);
                }
            }
        }
    }

    private Oauth2Provider createProvider(String name, Oauth2ProviderConfig config) {
        switch (name.toLowerCase()) {
            case "github":
                return new GithubProvider(config);
            case "google":
                return new GoogleProvider(config);
            case "wechat":
                return new WechatProvider(config);
            case "dingtalk":
                return new DingtalkProvider(config);
            case "feishu":
                return new FeishuProvider(config);
            case "gitee":
                return new GiteeProvider(config);
            case "microsoft":
                return new MicrosoftProvider(config);
            default:
                return new GenericProvider(name, config);
        }
    }

    public Oauth2Provider getProvider(String name) {
        Oauth2Provider provider = providerMap.get(name.toLowerCase());
        if (provider == null) {
            throw new RuntimeException("不支持的OAuth2提供方: " + name);
        }
        return provider;
    }

    public List<Map<String, String>> getAvailableProviders() {
        List<Map<String, String>> list = new ArrayList<>();
        for (Map.Entry<String, Oauth2Provider> entry : providerMap.entrySet()) {
            Map<String, String> info = new HashMap<>();
            info.put("name", entry.getKey());
            info.put("displayName", getDisplayName(entry.getKey()));
            list.add(info);
        }
        return list;
    }

    private String getDisplayName(String name) {
        switch (name.toLowerCase()) {
            case "github": return "GitHub";
            case "google": return "Google";
            case "wechat": return "微信";
            case "dingtalk": return "钉钉";
            case "feishu": return "飞书";
            case "gitee": return "Gitee";
            case "microsoft": return "Microsoft";
            default: return name;
        }
    }

    public Map<String, Oauth2ProviderConfig> getProviders() {
        return providers;
    }

    public void setProviders(Map<String, Oauth2ProviderConfig> providers) {
        this.providers = providers;
    }

    public String getFrontendRedirectUrl() {
        return frontendRedirectUrl;
    }

    public void setFrontendRedirectUrl(String frontendRedirectUrl) {
        this.frontendRedirectUrl = frontendRedirectUrl;
    }

    private static class GenericProvider extends AbstractOauth2Provider {
        private final String name;

        GenericProvider(String name, Oauth2ProviderConfig config) {
            super(config);
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
