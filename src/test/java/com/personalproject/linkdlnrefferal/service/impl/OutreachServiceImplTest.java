package com.personalproject.linkdlnrefferal.service.impl;

import com.personalproject.linkdlnrefferal.dto.request.OutreachRequest;
import com.personalproject.linkdlnrefferal.dto.response.OutreachResponse;
import com.personalproject.linkdlnrefferal.entity.OutreachMessage;
import com.personalproject.linkdlnrefferal.entity.UserAccount;
import com.personalproject.linkdlnrefferal.exception.ResourceNotFoundException;
import com.personalproject.linkdlnrefferal.mapper.OutreachMessageMapper;
import com.personalproject.linkdlnrefferal.repository.OutreachMessageRepository;
import com.personalproject.linkdlnrefferal.security.AuthenticatedUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OutreachServiceImplTest {

    @Mock
    private OutreachMessageRepository outreachMessageRepository;

    @Mock
    private OutreachMessageMapper outreachMessageMapper;

    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @InjectMocks
    private OutreachServiceImpl outreachService;

    @Test
    void createAssignsAuthenticatedUserIdAndDefaults() {
        when(authenticatedUserService.getCurrentUserAccount()).thenReturn(currentUser());
        when(outreachMessageRepository.save(any(OutreachMessage.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(outreachMessageMapper.toResponse(any(OutreachMessage.class))).thenReturn(new OutreachResponse());

        outreachService.create(request());

        ArgumentCaptor<OutreachMessage> captor = ArgumentCaptor.forClass(OutreachMessage.class);
        verify(outreachMessageRepository).save(captor.capture());
        assertThat(captor.getValue().getUserId()).isEqualTo(42L);
        assertThat(captor.getValue().getInviteStatus()).isEqualTo("Not Sent");
        assertThat(captor.getValue().getEmailStatus()).isEqualTo("Draft");
    }

    @Test
    void updateRejectsAnotherUsersRecord() {
        when(authenticatedUserService.getCurrentUserAccount()).thenReturn(currentUser());
        when(outreachMessageRepository.findByIdAndUserId(7L, 42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> outreachService.update(7L, request()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Outreach message not found with ID: 7");
    }

    private UserAccount currentUser() {
        UserAccount user = new UserAccount();
        user.setId(42L);
        return user;
    }

    private OutreachRequest request() {
        OutreachRequest request = new OutreachRequest();
        request.setContactName("Ada Lovelace");
        request.setCompany("Analytical Engines");
        request.setProfileUrl("https://www.linkedin.com/in/ada-lovelace");
        return request;
    }
}
