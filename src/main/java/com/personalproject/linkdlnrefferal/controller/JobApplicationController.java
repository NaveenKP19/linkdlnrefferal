package com.personalproject.linkdlnrefferal.controller;

import com.personalproject.linkdlnrefferal.dto.request.JobApplicationRequest;
import com.personalproject.linkdlnrefferal.dto.request.StatusRequest;
import com.personalproject.linkdlnrefferal.dto.response.JobApplicationResponse;
import com.personalproject.linkdlnrefferal.service.JobApplicationService;
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
@RequestMapping("/api/v1/job-applications")
@RequiredArgsConstructor
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    @PostMapping
    public ResponseEntity<JobApplicationResponse> create(@Valid @RequestBody JobApplicationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(jobApplicationService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<JobApplicationResponse>> getAll() {
        return ResponseEntity.ok(jobApplicationService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobApplicationResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(jobApplicationService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobApplicationResponse> update(@PathVariable Long id, @Valid @RequestBody JobApplicationRequest request) {
        return ResponseEntity.ok(jobApplicationService.update(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<JobApplicationResponse> updateStatus(@PathVariable Long id, @Valid @RequestBody StatusRequest request) {
        return ResponseEntity.ok(jobApplicationService.updateStatus(id, request.getStatus()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        jobApplicationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
