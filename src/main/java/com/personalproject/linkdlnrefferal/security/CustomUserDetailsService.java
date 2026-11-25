package com.personalproject.linkdlnrefferal.security;

import com.personalproject.linkdlnrefferal.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userAccountRepository.findByEmailIgnoreCase(email)
                .filter(user -> Boolean.TRUE.equals(user.getActive()))
                .map(UserAccountPrincipal::new)
                .orElseThrow(() -> new UsernameNotFoundException("Active user account not found"));
    }
}
