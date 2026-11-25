package com.personalproject.linkdlnrefferal.service.impl;

import com.personalproject.linkdlnrefferal.dto.request.MessageTemplateRequest;
import com.personalproject.linkdlnrefferal.dto.response.MessageTemplateResponse;
import com.personalproject.linkdlnrefferal.entity.MessageTemplate;
import com.personalproject.linkdlnrefferal.exception.ResourceNotFoundException;
import com.personalproject.linkdlnrefferal.mapper.MessageTemplateMapper;
import com.personalproject.linkdlnrefferal.repository.MessageTemplateRepository;
import com.personalproject.linkdlnrefferal.security.AuthenticatedUserService;
import com.personalproject.linkdlnrefferal.service.MessageTemplateService;
import com.personalproject.linkdlnrefferal.util.ValueUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageTemplateServiceImpl implements MessageTemplateService {

    private final MessageTemplateRepository messageTemplateRepository;
    private final MessageTemplateMapper messageTemplateMapper;
    private final AuthenticatedUserService authenticatedUserService;

    @Override
    public MessageTemplateResponse create(MessageTemplateRequest request) {
        MessageTemplate template = new MessageTemplate();
        template.setUserId(currentUserId());
        applyRequest(template, request);
        return messageTemplateMapper.toResponse(messageTemplateRepository.save(template));
    }

    @Override
    public List<MessageTemplateResponse> getAll() {
        return messageTemplateRepository.findAllByUserIdOrderByCreatedAtDesc(currentUserId())
                .stream()
                .map(messageTemplateMapper::toResponse)
                .toList();
    }

    @Override
    public MessageTemplateResponse getById(Long id) {
        return messageTemplateMapper.toResponse(getOwned(id));
    }

    @Override
    public MessageTemplateResponse update(Long id, MessageTemplateRequest request) {
        MessageTemplate template = getOwned(id);
        applyRequest(template, request);
        return messageTemplateMapper.toResponse(messageTemplateRepository.save(template));
    }

    @Override
    public void delete(Long id) {
        messageTemplateRepository.delete(getOwned(id));
    }

    @Override
    public MessageTemplateResponse setFavorite(Long id, Boolean favorite) {
        MessageTemplate template = getOwned(id);
        template.setFavorite(Boolean.TRUE.equals(favorite));
        return messageTemplateMapper.toResponse(messageTemplateRepository.save(template));
    }

    private MessageTemplate getOwned(Long id) {
        return messageTemplateRepository.findByIdAndUserId(id, currentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Message template not found with ID: " + id));
    }

    private Long currentUserId() {
        return authenticatedUserService.getCurrentUserAccount().getId();
    }

    private void applyRequest(MessageTemplate template, MessageTemplateRequest request) {
        template.setTitle(ValueUtils.clean(request.getTitle()));
        template.setCategory(ValueUtils.clean(request.getCategory(), "Custom"));
        template.setSubject(ValueUtils.clean(request.getSubject()));
        template.setBody(ValueUtils.clean(request.getBody()));
        template.setVariables(ValueUtils.cleanList(request.getVariables()));
        template.setTags(ValueUtils.cleanList(request.getTags()));

        if (request.getIsFavorite() != null) {
            template.setFavorite(request.getIsFavorite());
        }
    }
}
