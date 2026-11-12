package com.personalproject.linkdlnrefferal.mapper;

import com.personalproject.linkdlnrefferal.dto.request.UserAccountRequest;
import com.personalproject.linkdlnrefferal.dto.response.UserAccountResponse;
import com.personalproject.linkdlnrefferal.entity.UserAccount;
import org.springframework.stereotype.Component;

@Component
public class UserAccountMapper {

    public UserAccountResponse toResponse(UserAccount user){
        UserAccountResponse response =new UserAccountResponse();

        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setLocation(user.getLocation());
        response.setLinkedinProfileUrl(user.getLinkedinProfileUrl());
        response.setActive(user.getActive());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());

        return response;
    }
}
