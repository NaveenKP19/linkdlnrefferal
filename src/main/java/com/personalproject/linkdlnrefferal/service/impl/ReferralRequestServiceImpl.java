package com.personalproject.linkdlnrefferal.service.impl;

import com.personalproject.linkdlnrefferal.dto.request.ReferralRequestRequest;
import com.personalproject.linkdlnrefferal.dto.response.ReferralRequestResponse;
import com.personalproject.linkdlnrefferal.entity.ReferralRequest;
import com.personalproject.linkdlnrefferal.exception.ResourceNotFoundException;
import com.personalproject.linkdlnrefferal.mapper.ReferralRequestMapper;
import com.personalproject.linkdlnrefferal.repository.ReferralRequestRepository;
import com.personalproject.linkdlnrefferal.security.AuthenticatedUserService;
import com.personalproject.linkdlnrefferal.service.ReferralRequestService;
import com.personalproject.linkdlnrefferal.util.ValueUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReferralRequestServiceImpl implements ReferralRequestService {

    private final ReferralRequestRepository referralRequestRepository;
    private final ReferralRequestMapper referralRequestMapper;
    private final AuthenticatedUserService authenticatedUserService;

    @Override
    public ReferralRequestResponse create(ReferralRequestRequest request) {
        ReferralRequest referralRequest = new ReferralRequest();
        referralRequest.setUserId(currentUserId());
        applyRequest(referralRequest, request);
        return referralRequestMapper.toResponse(referralRequestRepository.save(referralRequest));
    }

    @Override
    public List<ReferralRequestResponse> getAll() {
        return referralRequestRepository.findAllByUserIdOrderByCreatedAtDesc(currentUserId())
                .stream()
                .map(referralRequestMapper::toResponse)
                .toList();
    }

    @Override
    public ReferralRequestResponse getById(Long id) {
        return referralRequestMapper.toResponse(getOwned(id));
    }

    @Override
    public ReferralRequestResponse update(Long id, ReferralRequestRequest request) {
        ReferralRequest referralRequest = getOwned(id);
        applyRequest(referralRequest, request);
        return referralRequestMapper.toResponse(referralRequestRepository.save(referralRequest));
    }

    @Override
    public void delete(Long id) {
        referralRequestRepository.delete(getOwned(id));
    }

    @Override
    public ReferralRequestResponse updateStatus(Long id, String status) {
        ReferralRequest referralRequest = getOwned(id);
        referralRequest.setStatus(ValueUtils.clean(status, "Draft"));
        return referralRequestMapper.toResponse(referralRequestRepository.save(referralRequest));
    }

    private ReferralRequest getOwned(Long id) {
        return referralRequestRepository.findByIdAndUserId(id, currentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Referral request not found with ID: " + id));
    }

    private Long currentUserId() {
        return authenticatedUserService.getCurrentUserAccount().getId();
    }

    private void applyRequest(ReferralRequest referralRequest, ReferralRequestRequest request) {
        referralRequest.setContactName(ValueUtils.clean(request.getContactName()));
        referralRequest.setCompany(ValueUtils.clean(request.getCompany()));
        referralRequest.setPosition(ValueUtils.clean(request.getPosition()));
        referralRequest.setConnectionId(request.getConnectionId());
        referralRequest.setOpportunityId(request.getOpportunityId());
        referralRequest.setRequestDate(request.getRequestDate() == null ? LocalDate.now() : request.getRequestDate());
        referralRequest.setStatus(ValueUtils.clean(request.getStatus(), "Draft"));
        referralRequest.setFollowUpDate(request.getFollowUpDate());
        referralRequest.setMessage(ValueUtils.clean(request.getMessage()));
        referralRequest.setNotes(ValueUtils.clean(request.getNotes()));
        referralRequest.setTags(ValueUtils.cleanList(request.getTags()));
    }
}
