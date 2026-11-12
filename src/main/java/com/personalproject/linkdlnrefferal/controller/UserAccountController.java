package com.personalproject.linkdlnrefferal.controller;

import com.personalproject.linkdlnrefferal.dto.request.UserAccountRequest;
import com.personalproject.linkdlnrefferal.dto.request.UserAccountUpdateRequest;
import com.personalproject.linkdlnrefferal.dto.response.UserAccountResponse;
import com.personalproject.linkdlnrefferal.service.UserAccountService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserAccountController {

    private final UserAccountService userAccountService;

    @PostMapping
    public ResponseEntity<UserAccountResponse> createUser(@Valid @RequestBody UserAccountRequest request){
        UserAccountResponse response = userAccountService.createUser(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                            .body(response);
    }

    @GetMapping
    public ResponseEntity<List<UserAccountResponse>> getAllUsers(){
        return ResponseEntity.ok(userAccountService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserAccountResponse> getUserById(@PathVariable Long id){
        return ResponseEntity.ok(userAccountService.getUserById(id));
    }

    @PutMapping("/{id}")
    public  ResponseEntity<UserAccountResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserAccountUpdateRequest request){
        return ResponseEntity.ok(userAccountService.updateUser(id,request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id){
        userAccountService.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }
}
