package com.personalproject.linkdlnrefferal.service;

import com.personalproject.linkdlnrefferal.dto.request.LoginRequest;
import com.personalproject.linkdlnrefferal.dto.request.RefreshTokenRequest;
import com.personalproject.linkdlnrefferal.dto.request.RegisterRequest;
import com.personalproject.linkdlnrefferal.dto.response.AuthResponse;
import com.personalproject.linkdlnrefferal.dto.response.UserAccountResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refresh(RefreshTokenRequest request);

    void logout(RefreshTokenRequest request);

    UserAccountResponse me(Long userId);

    AuthResponse issueTokensForUserId(Long userId);
}
