package com.personalproject.linkdlnrefferal.service.impl;

import com.personalproject.linkdlnrefferal.dto.request.UserAccountRequest;
import com.personalproject.linkdlnrefferal.dto.request.UserAccountUpdateRequest;
import com.personalproject.linkdlnrefferal.dto.response.UserAccountResponse;
import com.personalproject.linkdlnrefferal.entity.UserAccount;
import com.personalproject.linkdlnrefferal.mapper.UserAccountMapper;
import com.personalproject.linkdlnrefferal.repository.UserAccountRepository;
import com.personalproject.linkdlnrefferal.service.UserAccountService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserAccountServiceImpl implements UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private  final UserAccountMapper userAccountMapper;
    private  final PasswordEncoder passwordEncoder;

    @Override
    public UserAccountResponse createUser(UserAccountRequest request) {
        String email = normalizeEmail(request.getEmail());

        if(userAccountRepository.existsByEmailIgnoreCase(email)){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Email is already registered");
        }

        UserAccount user =new UserAccount();

        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhoneNumber(request.getPhoneNumber());
        user.setLocation(request.getLocation());
        user.setLinkedinProfileUrl(request.getLinkedinProfileUrl());
        user.setActive(true);

        UserAccount savedUser = userAccountRepository.save(user);

        return userAccountMapper.toResponse(savedUser);
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
        UserAccount user =getActiveUser(id);
        return userAccountMapper.toResponse(user);
    }

    @Override
    public UserAccountResponse updateUser(Long id, UserAccountUpdateRequest request) {
      UserAccount user =getActiveUser(id);

      String email =normalizeEmail(request.getEmail());

      userAccountRepository.findByEmailIgnoreCase(email)
              .filter(existing -> !existing.getId().equals(id))
              .ifPresent(existing -> {
                  throw new ResponseStatusException(
                          HttpStatus.CONFLICT,
                          "Email is already registered"
                  );
              });

      user.setFirstName(request.getFirstName().trim());
      user.setLastName(request.getLastName().trim());
      user.setEmail(request.getEmail());
      user.setPhoneNumber(request.getPhoneNumber());
      user.setLocation(request.getLocation());
      user.setLinkedinProfileUrl(request.getLinkedinProfileUrl());

      UserAccount updateUser = userAccountRepository.save(user);

      return userAccountMapper.toResponse(updateUser);
    }

    @Override
    public void deactivateUser(Long id) {

        UserAccount user = getActiveUser(id);

        user.setActive(false);

        userAccountRepository.save(user);

    }

    private  UserAccount getActiveUser(Long id){
        return userAccountRepository.findByIdAndActiveTrue(id)
                .orElseThrow(()->new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User account not found with ID: " + id
                ));
    }

    private String normalizeEmail(String email) {
        return email == null
                ? null
                : email.trim().toLowerCase(java.util.Locale.ROOT);
    }
}
