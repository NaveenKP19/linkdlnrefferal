package com.personalproject.linkdlnrefferal.service.impl;

import com.personalproject.linkdlnrefferal.dto.response.FollowUpResponse;
import com.personalproject.linkdlnrefferal.entity.FollowUp;
import com.personalproject.linkdlnrefferal.entity.UserAccount;
import com.personalproject.linkdlnrefferal.mapper.FollowUpMapper;
import com.personalproject.linkdlnrefferal.repository.FollowUpRepository;
import com.personalproject.linkdlnrefferal.security.AuthenticatedUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FollowUpServiceImplTest {

    @Mock
    private FollowUpRepository followUpRepository;

    @Mock
    private FollowUpMapper followUpMapper;

    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @InjectMocks
    private FollowUpServiceImpl followUpService;

    @Test
    void completeMarksFollowUpCompleted() {
        UserAccount user = new UserAccount();
        user.setId(42L);

        FollowUp followUp = new FollowUp();
        followUp.setId(9L);
        followUp.setUserId(42L);
        followUp.setDueDate(LocalDate.now().minusDays(1));
        followUp.setStatus("Overdue");

        when(authenticatedUserService.getCurrentUserAccount()).thenReturn(user);
        when(followUpRepository.findByIdAndUserId(9L, 42L)).thenReturn(Optional.of(followUp));
        when(followUpRepository.save(any(FollowUp.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(followUpMapper.toResponse(any(FollowUp.class))).thenReturn(new FollowUpResponse());

        followUpService.complete(9L);

        assertThat(followUp.getStatus()).isEqualTo("Completed");
        assertThat(followUp.getCompletedDate()).isEqualTo(LocalDate.now());
    }

    @Test
    void reopenClearsCompletedDate() {
        UserAccount user = new UserAccount();
        user.setId(42L);

        FollowUp followUp = new FollowUp();
        followUp.setId(9L);
        followUp.setUserId(42L);
        followUp.setDueDate(LocalDate.now().plusDays(2));
        followUp.setCompletedDate(LocalDate.now());
        followUp.setStatus("Completed");

        when(authenticatedUserService.getCurrentUserAccount()).thenReturn(user);
        when(followUpRepository.findByIdAndUserId(9L, 42L)).thenReturn(Optional.of(followUp));
        when(followUpRepository.save(any(FollowUp.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(followUpMapper.toResponse(any(FollowUp.class))).thenReturn(new FollowUpResponse());

        followUpService.reopen(9L);

        assertThat(followUp.getCompletedDate()).isNull();
        assertThat(followUp.getStatus()).isEqualTo("Upcoming");
    }
}
