package com.admin.mapper;

import com.admin.entity.Oauth2User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface Oauth2UserMapper {

    Oauth2User selectByProviderAndProviderId(@Param("provider") String provider, @Param("providerId") String providerId);

    Oauth2User selectByUserIdAndProvider(@Param("userId") Long userId, @Param("provider") String provider);

    int insert(Oauth2User oauth2User);
}
