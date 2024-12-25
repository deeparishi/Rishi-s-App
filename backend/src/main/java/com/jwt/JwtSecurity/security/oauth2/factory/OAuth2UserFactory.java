package com.jwt.JwtSecurity.security.oauth2.factory;

import com.jwt.JwtSecurity.enums.Enums;
import com.jwt.JwtSecurity.security.oauth2.userInfo.GithubOAuth2UserInfo;
import com.jwt.JwtSecurity.security.oauth2.userInfo.GoogleOAuth2UserInfo;
import com.jwt.JwtSecurity.security.oauth2.userInfo.OAuth2UserInfo;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

import java.util.Map;

public class OAuth2UserFactory {

    public static OAuth2UserInfo getOAuth2User(String registrationId, Map<String, Object> attributes) {
        if(registrationId.equalsIgnoreCase(Enums.AuthProvider.GOOGLE.toString())){
            return new GoogleOAuth2UserInfo(attributes);
        }else if(registrationId.equalsIgnoreCase((Enums.AuthProvider.GITHUB.toString()))){
            return new GithubOAuth2UserInfo(attributes);
        } else {
            throw new OAuth2AuthenticationException("Sorry! Login with " + registrationId + " is not supported yet.");
        }
    }

}
