package com.personalproject.linkdlnrefferal.controller;

import com.personalproject.linkdlnrefferal.dto.request.ReferralRequestRequest;
import com.personalproject.linkdlnrefferal.dto.request.StatusRequest;
import com.personalproject.linkdlnrefferal.dto.response.ReferralRequestResponse;
import com.personalproject.linkdlnrefferal.service.ReferralRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/referral-requests")
@RequiredArgsConstructor
public class ReferralRequestController {

    private final ReferralRequestService referralRequestService;

    @PostMapping
    public ResponseEntity<ReferralRequestResponse> create(@Valid @RequestBody ReferralRequestRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(referralRequestService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<ReferralRequestResponse>> getAll() {
        return ResponseEntity.ok(referralRequestService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReferralRequestResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(referralRequestService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReferralRequestResponse> update(@PathVariable Long id, @Valid @RequestBody ReferralRequestRequest request) {
        return ResponseEntity.ok(referralRequestService.update(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ReferralRequestResponse> updateStatus(@PathVariable Long id, @Valid @RequestBody StatusRequest request) {
        return ResponseEntity.ok(referralRequestService.updateStatus(id, request.getStatus()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        referralRequestService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
