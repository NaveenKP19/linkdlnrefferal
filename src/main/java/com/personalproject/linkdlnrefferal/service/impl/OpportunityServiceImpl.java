package com.personalproject.linkdlnrefferal.service.impl;

import com.personalproject.linkdlnrefferal.dto.request.OpportunityRequest;
import com.personalproject.linkdlnrefferal.dto.response.OpportunityResponse;
import com.personalproject.linkdlnrefferal.entity.Opportunity;
import com.personalproject.linkdlnrefferal.exception.ResourceNotFoundException;
import com.personalproject.linkdlnrefferal.mapper.OpportunityMapper;
import com.personalproject.linkdlnrefferal.repository.OpportunityRepository;
import com.personalproject.linkdlnrefferal.security.AuthenticatedUserService;
import com.personalproject.linkdlnrefferal.service.OpportunityService;
import com.personalproject.linkdlnrefferal.util.ValueUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OpportunityServiceImpl implements OpportunityService {

    private final OpportunityRepository opportunityRepository;
    private final OpportunityMapper opportunityMapper;
    private final AuthenticatedUserService authenticatedUserService;

    @Override
    public OpportunityResponse create(OpportunityRequest request) {
        Opportunity opportunity = new Opportunity();
        opportunity.setUserId(currentUserId());
        applyRequest(opportunity, request);
        return opportunityMapper.toResponse(opportunityRepository.save(opportunity));
    }

    @Override
    public List<OpportunityResponse> getAll() {
        return opportunityRepository.findAllByUserIdOrderByCreatedAtDesc(currentUserId())
                .stream()
                .map(opportunityMapper::toResponse)
                .toList();
    }

    @Override
    public OpportunityResponse getById(Long id) {
        return opportunityMapper.toResponse(getOwned(id));
    }

    @Override
    public OpportunityResponse update(Long id, OpportunityRequest request) {
        Opportunity opportunity = getOwned(id);
        applyRequest(opportunity, request);
        return opportunityMapper.toResponse(opportunityRepository.save(opportunity));
    }

    @Override
    public void delete(Long id) {
        opportunityRepository.delete(getOwned(id));
    }

    @Override
    public OpportunityResponse archive(Long id) {
        Opportunity opportunity = getOwned(id);
        opportunity.setStatus("Archived");
        return opportunityMapper.toResponse(opportunityRepository.save(opportunity));
    }

    private Opportunity getOwned(Long id) {
        return opportunityRepository.findByIdAndUserId(id, currentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found with ID: " + id));
    }

    private Long currentUserId() {
        return authenticatedUserService.getCurrentUserAccount().getId();
    }

    private void applyRequest(Opportunity opportunity, OpportunityRequest request) {
        opportunity.setCompany(ValueUtils.clean(request.getCompany()));
        opportunity.setRole(ValueUtils.clean(request.getRole()));
        opportunity.setLocation(ValueUtils.clean(request.getLocation()));
        opportunity.setEmploymentType(ValueUtils.clean(request.getEmploymentType(), "Full-time"));
        opportunity.setWorkMode(ValueUtils.clean(request.getWorkMode(), "Remote"));
        opportunity.setSalaryRange(ValueUtils.clean(request.getSalaryRange()));
        opportunity.setStatus(ValueUtils.clean(request.getStatus(), "New"));
        opportunity.setMatchScore(request.getMatchScore() == null ? 0 : request.getMatchScore());
        opportunity.setContactName(ValueUtils.clean(request.getContactName()));
        opportunity.setPostingUrl(ValueUtils.clean(request.getPostingUrl()));
        opportunity.setTags(ValueUtils.cleanList(request.getTags()));
        opportunity.setNotes(ValueUtils.clean(request.getNotes()));
    }
}
