package com.personalproject.linkdlnrefferal.service.impl;

import com.personalproject.linkdlnrefferal.dto.request.ConnectionRequest;
import com.personalproject.linkdlnrefferal.dto.response.ConnectionResponse;
import com.personalproject.linkdlnrefferal.entity.Connection;
import com.personalproject.linkdlnrefferal.entity.UserAccount;
import com.personalproject.linkdlnrefferal.exception.ResourceNotFoundException;
import com.personalproject.linkdlnrefferal.mapper.ConnectionMapper;
import com.personalproject.linkdlnrefferal.repository.ConnectionRepository;
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
class ConnectionServiceImplTest {

    @Mock
    private ConnectionRepository connectionRepository;

    @Mock
    private ConnectionMapper connectionMapper;

    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @InjectMocks
    private ConnectionServiceImpl connectionService;

    @Test
    void createAssignsAuthenticatedUserId() {
        when(authenticatedUserService.getCurrentUserAccount()).thenReturn(currentUser());
        when(connectionRepository.save(any(Connection.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(connectionMapper.toResponse(any(Connection.class))).thenReturn(new ConnectionResponse());

        connectionService.create(request());

        ArgumentCaptor<Connection> captor = ArgumentCaptor.forClass(Connection.class);
        verify(connectionRepository).save(captor.capture());
        assertThat(captor.getValue().getUserId()).isEqualTo(42L);
        assertThat(captor.getValue().getFirstName()).isEqualTo("Ada");
    }

    @Test
    void updateRejectsAnotherUsersRecord() {
        when(authenticatedUserService.getCurrentUserAccount()).thenReturn(currentUser());
        when(connectionRepository.findByIdAndUserId(7L, 42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> connectionService.update(7L, request()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Connection not found with ID: 7");
    }

    private UserAccount currentUser() {
        UserAccount user = new UserAccount();
        user.setId(42L);
        return user;
    }

    private ConnectionRequest request() {
        ConnectionRequest request = new ConnectionRequest();
        request.setFirstName("Ada");
        request.setLastName("Lovelace");
        request.setEmail("ada@example.com");
        return request;
    }
}
