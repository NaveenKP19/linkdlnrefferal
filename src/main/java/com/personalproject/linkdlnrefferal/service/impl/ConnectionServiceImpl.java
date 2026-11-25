package com.personalproject.linkdlnrefferal.service.impl;

import com.personalproject.linkdlnrefferal.dto.request.ConnectionRequest;
import com.personalproject.linkdlnrefferal.dto.response.ConnectionResponse;
import com.personalproject.linkdlnrefferal.entity.Connection;
import com.personalproject.linkdlnrefferal.exception.ResourceNotFoundException;
import com.personalproject.linkdlnrefferal.mapper.ConnectionMapper;
import com.personalproject.linkdlnrefferal.repository.ConnectionRepository;
import com.personalproject.linkdlnrefferal.security.AuthenticatedUserService;
import com.personalproject.linkdlnrefferal.service.ConnectionService;
import com.personalproject.linkdlnrefferal.util.ValueUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ConnectionServiceImpl implements ConnectionService {

    private final ConnectionRepository connectionRepository;
    private final ConnectionMapper connectionMapper;
    private final AuthenticatedUserService authenticatedUserService;

    @Override
    public ConnectionResponse create(ConnectionRequest request) {
        Connection connection = new Connection();
        connection.setUserId(currentUserId());
        applyRequest(connection, request);
        return connectionMapper.toResponse(connectionRepository.save(connection));
    }

    @Override
    public List<ConnectionResponse> getAll() {
        return connectionRepository.findAllByUserIdOrderByCreatedAtDesc(currentUserId())
                .stream()
                .map(connectionMapper::toResponse)
                .toList();
    }

    @Override
    public ConnectionResponse getById(Long id) {
        return connectionMapper.toResponse(getOwned(id));
    }

    @Override
    public ConnectionResponse update(Long id, ConnectionRequest request) {
        Connection connection = getOwned(id);
        applyRequest(connection, request);
        return connectionMapper.toResponse(connectionRepository.save(connection));
    }

    @Override
    public void delete(Long id) {
        connectionRepository.delete(getOwned(id));
    }

    private Connection getOwned(Long id) {
        return connectionRepository.findByIdAndUserId(id, currentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Connection not found with ID: " + id));
    }

    private Long currentUserId() {
        return authenticatedUserService.getCurrentUserAccount().getId();
    }

    private void applyRequest(Connection connection, ConnectionRequest request) {
        connection.setFirstName(ValueUtils.clean(request.getFirstName()));
        connection.setLastName(ValueUtils.clean(request.getLastName()));
        connection.setEmail(ValueUtils.clean(request.getEmail()));
        connection.setCompany(ValueUtils.clean(request.getCompany()));
        connection.setJobTitle(ValueUtils.clean(request.getJobTitle()));
        connection.setLocation(ValueUtils.clean(request.getLocation()));
        connection.setLinkedinProfileUrl(ValueUtils.clean(request.getLinkedinProfileUrl()));
        connection.setCategory(ValueUtils.clean(request.getCategory(), "Other"));
        connection.setStrength(ValueUtils.clean(request.getStrength(), "Moderate"));
        connection.setTags(ValueUtils.cleanList(request.getTags()));
        connection.setNotes(ValueUtils.clean(request.getNotes()));
    }
}
