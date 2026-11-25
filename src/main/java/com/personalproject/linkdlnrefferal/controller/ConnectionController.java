package com.personalproject.linkdlnrefferal.controller;

import com.personalproject.linkdlnrefferal.dto.request.ConnectionRequest;
import com.personalproject.linkdlnrefferal.dto.response.ConnectionResponse;
import com.personalproject.linkdlnrefferal.service.ConnectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/connections")
@RequiredArgsConstructor
public class ConnectionController {

    private final ConnectionService connectionService;

    @PostMapping
    public ResponseEntity<ConnectionResponse> create(@Valid @RequestBody ConnectionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(connectionService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<ConnectionResponse>> getAll() {
        return ResponseEntity.ok(connectionService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConnectionResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(connectionService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConnectionResponse> update(@PathVariable Long id, @Valid @RequestBody ConnectionRequest request) {
        return ResponseEntity.ok(connectionService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        connectionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
