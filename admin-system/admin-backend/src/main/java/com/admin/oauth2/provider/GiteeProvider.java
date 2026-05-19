package com.admin.oauth2.provider;

import com.admin.oauth2.AbstractOauth2Provider;
import com.admin.oauth2.Oauth2ProviderConfig;

public class GiteeProvider extends AbstractOauth2Provider {

    public GiteeProvider(Oauth2ProviderConfig config) {
        super(config);
    }

    @Override
    public String getName() {
        return "gitee";
    }
}
