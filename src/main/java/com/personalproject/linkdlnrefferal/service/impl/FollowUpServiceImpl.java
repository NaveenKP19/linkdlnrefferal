package com.personalproject.linkdlnrefferal.service.impl;

import com.personalproject.linkdlnrefferal.dto.request.FollowUpRequest;
import com.personalproject.linkdlnrefferal.dto.response.FollowUpResponse;
import com.personalproject.linkdlnrefferal.entity.FollowUp;
import com.personalproject.linkdlnrefferal.exception.ResourceNotFoundException;
import com.personalproject.linkdlnrefferal.mapper.FollowUpMapper;
import com.personalproject.linkdlnrefferal.repository.FollowUpRepository;
import com.personalproject.linkdlnrefferal.security.AuthenticatedUserService;
import com.personalproject.linkdlnrefferal.service.FollowUpService;
import com.personalproject.linkdlnrefferal.util.ValueUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FollowUpServiceImpl implements FollowUpService {

    private final FollowUpRepository followUpRepository;
    private final FollowUpMapper followUpMapper;
    private final AuthenticatedUserService authenticatedUserService;

    @Override
    public FollowUpResponse create(FollowUpRequest request) {
        FollowUp followUp = new FollowUp();
        followUp.setUserId(currentUserId());
        applyRequest(followUp, request);
        return followUpMapper.toResponse(followUpRepository.save(followUp));
    }

    @Override
    public List<FollowUpResponse> getAll() {
        return followUpRepository.findAllByUserIdOrderByCreatedAtDesc(currentUserId())
                .stream()
                .map(followUpMapper::toResponse)
                .toList();
    }

    @Override
    public FollowUpResponse getById(Long id) {
        return followUpMapper.toResponse(getOwned(id));
    }

    @Override
    public FollowUpResponse update(Long id, FollowUpRequest request) {
        FollowUp followUp = getOwned(id);
        applyRequest(followUp, request);
        return followUpMapper.toResponse(followUpRepository.save(followUp));
    }

    @Override
    public void delete(Long id) {
        followUpRepository.delete(getOwned(id));
    }

    @Override
    public FollowUpResponse complete(Long id) {
        FollowUp followUp = getOwned(id);
        followUp.setCompletedDate(LocalDate.now());
        followUp.setStatus("Completed");
        return followUpMapper.toResponse(followUpRepository.save(followUp));
    }

    @Override
    public FollowUpResponse reopen(Long id) {
        FollowUp followUp = getOwned(id);
        followUp.setCompletedDate(null);
        followUp.setStatus(resolveStatus(followUp.getDueDate()));
        return followUpMapper.toResponse(followUpRepository.save(followUp));
    }

    private FollowUp getOwned(Long id) {
        return followUpRepository.findByIdAndUserId(id, currentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Follow-up not found with ID: " + id));
    }

    private Long currentUserId() {
        return authenticatedUserService.getCurrentUserAccount().getId();
    }

    private void applyRequest(FollowUp followUp, FollowUpRequest request) {
        followUp.setContactName(ValueUtils.clean(request.getContactName()));
        followUp.setCompany(ValueUtils.clean(request.getCompany()));
        followUp.setRelatedRequestId(request.getRelatedRequestId());
        followUp.setDueDate(request.getDueDate() == null ? LocalDate.now() : request.getDueDate());
        followUp.setPriority(ValueUtils.clean(request.getPriority(), "Medium"));
        followUp.setMessage(ValueUtils.clean(request.getMessage()));
        followUp.setNotes(ValueUtils.clean(request.getNotes()));
        followUp.setStatus(resolveStatus(followUp.getDueDate()));
    }

    private String resolveStatus(LocalDate dueDate) {
        LocalDate today = LocalDate.now();

        if (dueDate == null || dueDate.isAfter(today)) {
            return "Upcoming";
        }
        if (dueDate.isEqual(today)) {
            return "Due Today";
        }
        return "Overdue";
    }
}
