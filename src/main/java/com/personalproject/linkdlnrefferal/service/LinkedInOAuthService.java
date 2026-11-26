package com.personalproject.linkdlnrefferal.service;

import com.personalproject.linkdlnrefferal.dto.request.LinkedInCompleteRegistrationRequest;
import com.personalproject.linkdlnrefferal.dto.request.LinkedInExchangeRequest;
import com.personalproject.linkdlnrefferal.dto.response.AuthResponse;
import com.personalproject.linkdlnrefferal.dto.response.LinkedInStatusResponse;

import java.net.URI;

public interface LinkedInOAuthService {

    String buildAuthorizationUrl(Long userId, String clientIp);

    URI handleCallback(String code, String state, String error, String clientIp);

    AuthResponse exchangeCode(LinkedInExchangeRequest request, String clientIp);

    AuthResponse completeRegistration(LinkedInCompleteRegistrationRequest request, String clientIp);

    LinkedInStatusResponse status(Long userId);

    void disconnect(Long userId);
}
