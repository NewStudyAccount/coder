package com.admin.oauth2.provider;

import com.admin.oauth2.AbstractOauth2Provider;
import com.admin.oauth2.Oauth2ProviderConfig;

public class MicrosoftProvider extends AbstractOauth2Provider {

    public MicrosoftProvider(Oauth2ProviderConfig config) {
        super(config);
    }

    @Override
    public String getName() {
        return "microsoft";
    }
}
