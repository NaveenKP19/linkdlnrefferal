package com.personalproject.linkdlnrefferal.security;

import com.personalproject.linkdlnrefferal.entity.UserAccount;
import com.personalproject.linkdlnrefferal.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticatedUserService {

    private final UserAccountRepository userAccountRepository;

    public UserAccount getCurrentUserAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof UserAccountPrincipal principal)) {
            throw new AccessDeniedException("Authenticated user required");
        }

        return userAccountRepository.findByIdAndActiveTrue(principal.getId())
                .orElseThrow(() -> new AccessDeniedException("Authenticated user not found"));
    }
}
