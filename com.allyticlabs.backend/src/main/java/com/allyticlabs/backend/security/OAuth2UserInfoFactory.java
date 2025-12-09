package com.allyticlabs.backend.security;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if (registrationId.equalsIgnoreCase("google")) {
            return new GoogleOAuth2UserInfo(attributes);
        }
        // Add more providers here (GitHub, Facebook, etc.)
        throw new IllegalArgumentException("Sorry! Login with " + registrationId + " is not supported yet.");
    }
}
