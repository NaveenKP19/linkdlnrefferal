package com.personalproject.linkdlnrefferal.controller;

import com.personalproject.linkdlnrefferal.dto.request.FavoriteRequest;
import com.personalproject.linkdlnrefferal.dto.request.MessageTemplateRequest;
import com.personalproject.linkdlnrefferal.dto.response.MessageTemplateResponse;
import com.personalproject.linkdlnrefferal.service.MessageTemplateService;
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
@RequestMapping("/api/v1/message-templates")
@RequiredArgsConstructor
public class MessageTemplateController {

    private final MessageTemplateService messageTemplateService;

    @PostMapping
    public ResponseEntity<MessageTemplateResponse> create(@Valid @RequestBody MessageTemplateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(messageTemplateService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<MessageTemplateResponse>> getAll() {
        return ResponseEntity.ok(messageTemplateService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageTemplateResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(messageTemplateService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageTemplateResponse> update(@PathVariable Long id, @Valid @RequestBody MessageTemplateRequest request) {
        return ResponseEntity.ok(messageTemplateService.update(id, request));
    }

    @PatchMapping("/{id}/favorite")
    public ResponseEntity<MessageTemplateResponse> setFavorite(@PathVariable Long id, @Valid @RequestBody FavoriteRequest request) {
        return ResponseEntity.ok(messageTemplateService.setFavorite(id, request.getIsFavorite()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        messageTemplateService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
