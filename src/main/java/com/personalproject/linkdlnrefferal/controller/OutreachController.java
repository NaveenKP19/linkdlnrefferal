package com.personalproject.linkdlnrefferal.controller;

import com.personalproject.linkdlnrefferal.dto.request.OutreachRequest;
import com.personalproject.linkdlnrefferal.dto.response.DashboardStatResponse;
import com.personalproject.linkdlnrefferal.dto.response.OutreachResponse;
import com.personalproject.linkdlnrefferal.service.OutreachService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/outreach")
@RequiredArgsConstructor
public class OutreachController {

    private final OutreachService outreachService;

    @GetMapping("/summary")
    public ResponseEntity<List<DashboardStatResponse>> getSummary(@RequestParam(required = false) String range) {
        return ResponseEntity.ok(outreachService.getSummary(range));
    }

    @PostMapping
    public ResponseEntity<OutreachResponse> create(@Valid @RequestBody OutreachRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(outreachService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<OutreachResponse>> getAll() {
        return ResponseEntity.ok(outreachService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OutreachResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(outreachService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OutreachResponse> update(@PathVariable Long id, @Valid @RequestBody OutreachRequest request) {
        return ResponseEntity.ok(outreachService.update(id, request));
    }

    @PatchMapping("/{id}/invite-sent")
    public ResponseEntity<OutreachResponse> markInviteSent(@PathVariable Long id) {
        return ResponseEntity.ok(outreachService.markInviteSent(id));
    }

    @PatchMapping("/{id}/accepted")
    public ResponseEntity<OutreachResponse> markAccepted(@PathVariable Long id) {
        return ResponseEntity.ok(outreachService.markAccepted(id));
    }

    @PatchMapping("/{id}/declined")
    public ResponseEntity<OutreachResponse> markDeclined(@PathVariable Long id) {
        return ResponseEntity.ok(outreachService.markDeclined(id));
    }

    @PatchMapping("/{id}/contacted")
    public ResponseEntity<OutreachResponse> markContacted(@PathVariable Long id) {
        return ResponseEntity.ok(outreachService.markContacted(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        outreachService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
