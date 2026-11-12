package com.personalproject.linkdlnrefferal.service;

import com.personalproject.linkdlnrefferal.dto.request.UserAccountRequest;
import com.personalproject.linkdlnrefferal.dto.request.UserAccountUpdateRequest;
import com.personalproject.linkdlnrefferal.dto.response.UserAccountResponse;

import java.util.List;

public interface UserAccountService {

    UserAccountResponse createUser(UserAccountRequest request);

    List<UserAccountResponse> getAllUsers();

    UserAccountResponse getUserById(Long id);

    UserAccountResponse updateUser(
            Long id,
            UserAccountUpdateRequest request
    );

    void deactivateUser(Long id);
}
