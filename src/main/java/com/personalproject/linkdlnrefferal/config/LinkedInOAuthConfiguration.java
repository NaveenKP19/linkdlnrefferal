package com.personalproject.linkdlnrefferal.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(LinkedInOAuthProperties.class)
public class LinkedInOAuthConfiguration {

    @Bean
    public RestClient linkedInRestClient() {
        return RestClient.builder().build();
    }
}
