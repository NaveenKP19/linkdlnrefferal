package com.personalproject.linkdlnrefferal.controller;

import com.personalproject.linkdlnrefferal.dto.request.LinkedInCompleteRegistrationRequest;
import com.personalproject.linkdlnrefferal.dto.request.LinkedInExchangeRequest;
import com.personalproject.linkdlnrefferal.dto.response.AuthResponse;
import com.personalproject.linkdlnrefferal.dto.response.LinkedInStartResponse;
import com.personalproject.linkdlnrefferal.dto.response.LinkedInStatusResponse;
import com.personalproject.linkdlnrefferal.exception.UnauthorizedException;
import com.personalproject.linkdlnrefferal.security.UserAccountPrincipal;
import com.personalproject.linkdlnrefferal.service.LinkedInOAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/auth/linkedin")
@RequiredArgsConstructor
public class LinkedInOAuthController {

    private final LinkedInOAuthService linkedInOAuthService;

    @GetMapping
    public ResponseEntity<Void> start(
            @AuthenticationPrincipal UserAccountPrincipal principal,
            HttpServletRequest request
    ) {
        Long userId = principal == null ? null : principal.getId();
        URI authorizationUrl = URI.create(linkedInOAuthService.buildAuthorizationUrl(userId, request.getRemoteAddr()));
        return ResponseEntity.status(HttpStatus.FOUND).location(authorizationUrl).build();
    }

    @PostMapping("/link")
    public LinkedInStartResponse startLink(
            @AuthenticationPrincipal UserAccountPrincipal principal,
            HttpServletRequest request
    ) {
        if (principal == null) {
            throw new UnauthorizedException("Authentication is required");
        }

        LinkedInStartResponse response = new LinkedInStartResponse();
        response.setAuthorizationUrl(
                linkedInOAuthService.buildAuthorizationUrl(principal.getId(), request.getRemoteAddr())
        );
        return response;
    }

    @GetMapping("/callback")
    public ResponseEntity<Void> callback(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String error,
            HttpServletRequest request
    ) {
        URI redirect = linkedInOAuthService.handleCallback(code, state, error, request.getRemoteAddr());
        return ResponseEntity.status(HttpStatus.FOUND).location(redirect).build();
    }

    @PostMapping("/exchange")
    public AuthResponse exchange(
            @Valid @RequestBody LinkedInExchangeRequest request,
            HttpServletRequest servletRequest
    ) {
        return linkedInOAuthService.exchangeCode(request, servletRequest.getRemoteAddr());
    }

    @PostMapping("/complete-registration")
    public AuthResponse completeRegistration(
            @Valid @RequestBody LinkedInCompleteRegistrationRequest request,
            HttpServletRequest servletRequest
    ) {
        return linkedInOAuthService.completeRegistration(request, servletRequest.getRemoteAddr());
    }

    @GetMapping("/status")
    public LinkedInStatusResponse status(@AuthenticationPrincipal UserAccountPrincipal principal) {
        if (principal == null) {
            throw new UnauthorizedException("Authentication is required");
        }
        return linkedInOAuthService.status(principal.getId());
    }

    @DeleteMapping("/disconnect")
    public ResponseEntity<Void> disconnect(@AuthenticationPrincipal UserAccountPrincipal principal) {
        if (principal == null) {
            throw new UnauthorizedException("Authentication is required");
        }
        linkedInOAuthService.disconnect(principal.getId());
        return ResponseEntity.noContent().build();
    }
}
