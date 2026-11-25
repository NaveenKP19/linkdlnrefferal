package com.personalproject.linkdlnrefferal.controller;

import com.personalproject.linkdlnrefferal.dto.request.FollowUpRequest;
import com.personalproject.linkdlnrefferal.dto.response.FollowUpResponse;
import com.personalproject.linkdlnrefferal.service.FollowUpService;
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
@RequestMapping("/api/v1/follow-ups")
@RequiredArgsConstructor
public class FollowUpController {

    private final FollowUpService followUpService;

    @PostMapping
    public ResponseEntity<FollowUpResponse> create(@Valid @RequestBody FollowUpRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(followUpService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<FollowUpResponse>> getAll() {
        return ResponseEntity.ok(followUpService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FollowUpResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(followUpService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FollowUpResponse> update(@PathVariable Long id, @Valid @RequestBody FollowUpRequest request) {
        return ResponseEntity.ok(followUpService.update(id, request));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<FollowUpResponse> complete(@PathVariable Long id) {
        return ResponseEntity.ok(followUpService.complete(id));
    }

    @PatchMapping("/{id}/reopen")
    public ResponseEntity<FollowUpResponse> reopen(@PathVariable Long id) {
        return ResponseEntity.ok(followUpService.reopen(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        followUpService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
