package com.personalproject.linkdlnrefferal.service.impl;

import com.personalproject.linkdlnrefferal.dto.request.UserAccountUpdateRequest;
import com.personalproject.linkdlnrefferal.dto.response.UserAccountResponse;
import com.personalproject.linkdlnrefferal.entity.UserAccount;
import com.personalproject.linkdlnrefferal.mapper.UserAccountMapper;
import com.personalproject.linkdlnrefferal.repository.UserAccountRepository;
import com.personalproject.linkdlnrefferal.security.AuthenticatedUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAccountServiceImplTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private UserAccountMapper userAccountMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @InjectMocks
    private UserAccountServiceImpl userService;

    @Test
    void updateUserSavesNormalizedEmail() {
        UserAccount currentUser = user(1L, "old@example.com");
        UserAccount targetUser = user(1L, "old@example.com");
        UserAccountUpdateRequest request = request("NEW@Example.com");

        UserAccountResponse response = new UserAccountResponse();
        response.setId(1L);
        response.setEmail("new@example.com");

        when(authenticatedUserService.getCurrentUserAccount()).thenReturn(currentUser);
        when(userAccountRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(targetUser));
        when(userAccountRepository.findByEmailIgnoreCase("new@example.com")).thenReturn(Optional.empty());
        when(userAccountRepository.save(any(UserAccount.class))).thenReturn(targetUser);
        when(userAccountMapper.toResponse(any(UserAccount.class))).thenReturn(response);

        UserAccountResponse result = userService.updateUser(1L, request);

        ArgumentCaptor<UserAccount> captor = ArgumentCaptor.forClass(UserAccount.class);
        verify(userAccountRepository).save(captor.capture());
        assertThat(captor.getValue().getEmail()).isEqualTo("new@example.com");
        assertThat(result.getEmail()).isEqualTo("new@example.com");
    }

    @Test
    void updateUserRejectsAnotherAccount() {
        when(authenticatedUserService.getCurrentUserAccount()).thenReturn(user(2L, "other@example.com"));

        assertThatThrownBy(() -> userService.updateUser(1L, request("new@example.com")))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Users can only update their own account");
    }

    private UserAccount user(Long id, String email) {
        UserAccount user = new UserAccount();
        user.setId(id);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail(email);
        user.setPassword("hash");
        user.setPhoneNumber("");
        user.setLocation("");
        user.setLinkedinProfileUrl("");
        user.setActive(true);
        return user;
    }

    private UserAccountUpdateRequest request(String email) {
        UserAccountUpdateRequest request = new UserAccountUpdateRequest();
        request.setFirstName("Test");
        request.setLastName("User");
        request.setEmail(email);
        request.setPhoneNumber("");
        request.setLocation("");
        request.setLinkedinProfileUrl("");
        return request;
    }
}
