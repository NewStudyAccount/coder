package com.admin.oauth2.provider;

import com.admin.oauth2.AbstractOauth2Provider;
import com.admin.oauth2.Oauth2ProviderConfig;

public class GithubProvider extends AbstractOauth2Provider {

    public GithubProvider(Oauth2ProviderConfig config) {
        super(config);
    }

    @Override
    public String getName() {
        return "github";
    }
}
