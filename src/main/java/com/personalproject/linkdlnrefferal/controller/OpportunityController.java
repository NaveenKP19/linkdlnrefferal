package com.personalproject.linkdlnrefferal.controller;

import com.personalproject.linkdlnrefferal.dto.request.OpportunityRequest;
import com.personalproject.linkdlnrefferal.dto.response.OpportunityResponse;
import com.personalproject.linkdlnrefferal.service.OpportunityService;
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
@RequestMapping("/api/v1/opportunities")
@RequiredArgsConstructor
public class OpportunityController {

    private final OpportunityService opportunityService;

    @PostMapping
    public ResponseEntity<OpportunityResponse> create(@Valid @RequestBody OpportunityRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(opportunityService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<OpportunityResponse>> getAll() {
        return ResponseEntity.ok(opportunityService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OpportunityResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(opportunityService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OpportunityResponse> update(@PathVariable Long id, @Valid @RequestBody OpportunityRequest request) {
        return ResponseEntity.ok(opportunityService.update(id, request));
    }

    @PatchMapping("/{id}/archive")
    public ResponseEntity<OpportunityResponse> archive(@PathVariable Long id) {
        return ResponseEntity.ok(opportunityService.archive(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        opportunityService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
