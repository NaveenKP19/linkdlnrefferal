package com.personalproject.linkdlnrefferal.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "linkedin")
public class LinkedInOAuthProperties {

    private String clientId = "";
    private String clientSecret = "";
    private String redirectUri = "http://localhost:8080/api/v1/auth/linkedin/callback";
    private List<String> scopes = List.of("openid", "profile", "email");
    private String authorizationEndpoint = "https://www.linkedin.com/oauth/v2/authorization";
    private String tokenEndpoint = "https://www.linkedin.com/oauth/v2/accessToken";
    private String userInfoEndpoint = "https://api.linkedin.com/v2/userinfo";
    private String discoveryEndpoint = "https://www.linkedin.com/oauth/.well-known/openid-configuration";
    private long stateTtlSeconds = 300;
    private long exchangeCodeTtlSeconds = 600;
}
