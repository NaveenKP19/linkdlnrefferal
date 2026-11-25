package com.personalproject.linkdlnrefferal.controller;

import com.personalproject.linkdlnrefferal.dto.request.CampaignMetricsRequest;
import com.personalproject.linkdlnrefferal.dto.request.CampaignRequest;
import com.personalproject.linkdlnrefferal.dto.request.StatusRequest;
import com.personalproject.linkdlnrefferal.dto.response.CampaignResponse;
import com.personalproject.linkdlnrefferal.service.CampaignService;
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
@RequestMapping("/api/v1/campaigns")
@RequiredArgsConstructor
public class CampaignController {

    private final CampaignService campaignService;

    @PostMapping
    public ResponseEntity<CampaignResponse> create(@Valid @RequestBody CampaignRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(campaignService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<CampaignResponse>> getAll() {
        return ResponseEntity.ok(campaignService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CampaignResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(campaignService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CampaignResponse> update(@PathVariable Long id, @Valid @RequestBody CampaignRequest request) {
        return ResponseEntity.ok(campaignService.update(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<CampaignResponse> updateStatus(@PathVariable Long id, @Valid @RequestBody StatusRequest request) {
        return ResponseEntity.ok(campaignService.updateStatus(id, request.getStatus()));
    }

    @PatchMapping("/{id}/metrics")
    public ResponseEntity<CampaignResponse> updateMetrics(@PathVariable Long id, @Valid @RequestBody CampaignMetricsRequest request) {
        return ResponseEntity.ok(campaignService.updateMetrics(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        campaignService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
