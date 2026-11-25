package com.personalproject.linkdlnrefferal.service.impl;

import com.personalproject.linkdlnrefferal.dto.request.UserAccountRequest;
import com.personalproject.linkdlnrefferal.dto.request.UserAccountUpdateRequest;
import com.personalproject.linkdlnrefferal.dto.response.UserAccountResponse;
import com.personalproject.linkdlnrefferal.entity.UserAccount;
import com.personalproject.linkdlnrefferal.exception.DuplicateResourceException;
import com.personalproject.linkdlnrefferal.exception.ResourceNotFoundException;
import com.personalproject.linkdlnrefferal.mapper.UserAccountMapper;
import com.personalproject.linkdlnrefferal.repository.UserAccountRepository;
import com.personalproject.linkdlnrefferal.security.AuthenticatedUserService;
import com.personalproject.linkdlnrefferal.service.UserAccountService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional
public class UserAccountServiceImpl implements UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final UserAccountMapper userAccountMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticatedUserService authenticatedUserService;

    @Override
    public UserAccountResponse createUser(UserAccountRequest request) {
        String email = normalizeEmail(request.getEmail());

        if (userAccountRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateResourceException("Email is already registered");
        }

        UserAccount user = new UserAccount();
        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhoneNumber(defaultIfBlank(request.getPhoneNumber()));
        user.setLocation(defaultIfBlank(request.getLocation()));
        user.setLinkedinProfileUrl(defaultIfBlank(request.getLinkedinProfileUrl()));
        user.setActive(true);

        return userAccountMapper.toResponse(userAccountRepository.save(user));
    }

    @Override
    public List<UserAccountResponse> getAllUsers() {
        return userAccountRepository
                .findAllByActiveTrueOrderByCreatedAtDesc()
                .stream()
                .map(userAccountMapper::toResponse)
                .toList();
    }

    @Override
    public UserAccountResponse getUserById(Long id) {
        return userAccountMapper.toResponse(getActiveUser(id));
    }

    @Override
    public UserAccountResponse updateUser(Long id, UserAccountUpdateRequest request) {
        UserAccount currentUser = authenticatedUserService.getCurrentUserAccount();

        if (!currentUser.getId().equals(id)) {
            throw new AccessDeniedException("Users can only update their own account");
        }

        UserAccount user = getActiveUser(id);
        String email = normalizeEmail(request.getEmail());

        userAccountRepository.findByEmailIgnoreCase(email)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new DuplicateResourceException("Email is already registered");
                });

        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setEmail(email);
        user.setPhoneNumber(defaultIfBlank(request.getPhoneNumber()));
        user.setLocation(defaultIfBlank(request.getLocation()));
        user.setLinkedinProfileUrl(defaultIfBlank(request.getLinkedinProfileUrl()));

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return userAccountMapper.toResponse(userAccountRepository.save(user));
    }

    @Override
    public void deactivateUser(Long id) {
        UserAccount currentUser = authenticatedUserService.getCurrentUserAccount();

        if (!currentUser.getId().equals(id)) {
            throw new AccessDeniedException("Users can only deactivate their own account");
        }

        UserAccount user = getActiveUser(id);
        user.setActive(false);
        userAccountRepository.save(user);
    }

    private UserAccount getActiveUser(Long id) {
        return userAccountRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("User account not found with ID: " + id));
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }

    private String defaultIfBlank(String value) {
        return value == null || value.isBlank() ? "" : value.trim();
    }
}
