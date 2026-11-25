package com.personalproject.linkdlnrefferal.service.impl;

import com.personalproject.linkdlnrefferal.dto.request.JobApplicationRequest;
import com.personalproject.linkdlnrefferal.dto.response.JobApplicationResponse;
import com.personalproject.linkdlnrefferal.entity.JobApplication;
import com.personalproject.linkdlnrefferal.exception.ResourceNotFoundException;
import com.personalproject.linkdlnrefferal.mapper.JobApplicationMapper;
import com.personalproject.linkdlnrefferal.repository.JobApplicationRepository;
import com.personalproject.linkdlnrefferal.security.AuthenticatedUserService;
import com.personalproject.linkdlnrefferal.service.JobApplicationService;
import com.personalproject.linkdlnrefferal.util.ValueUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class JobApplicationServiceImpl implements JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final JobApplicationMapper jobApplicationMapper;
    private final AuthenticatedUserService authenticatedUserService;

    @Override
    public JobApplicationResponse create(JobApplicationRequest request) {
        JobApplication application = new JobApplication();
        application.setUserId(currentUserId());
        applyRequest(application, request);
        return jobApplicationMapper.toResponse(jobApplicationRepository.save(application));
    }

    @Override
    public List<JobApplicationResponse> getAll() {
        return jobApplicationRepository.findAllByUserIdOrderByCreatedAtDesc(currentUserId())
                .stream()
                .map(jobApplicationMapper::toResponse)
                .toList();
    }

    @Override
    public JobApplicationResponse getById(Long id) {
        return jobApplicationMapper.toResponse(getOwned(id));
    }

    @Override
    public JobApplicationResponse update(Long id, JobApplicationRequest request) {
        JobApplication application = getOwned(id);
        applyRequest(application, request);
        return jobApplicationMapper.toResponse(jobApplicationRepository.save(application));
    }

    @Override
    public void delete(Long id) {
        jobApplicationRepository.delete(getOwned(id));
    }

    @Override
    public JobApplicationResponse updateStatus(Long id, String status) {
        JobApplication application = getOwned(id);
        application.setStatus(ValueUtils.clean(status, "Applied"));
        return jobApplicationMapper.toResponse(jobApplicationRepository.save(application));
    }

    private JobApplication getOwned(Long id) {
        return jobApplicationRepository.findByIdAndUserId(id, currentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Job application not found with ID: " + id));
    }

    private Long currentUserId() {
        return authenticatedUserService.getCurrentUserAccount().getId();
    }

    private void applyRequest(JobApplication application, JobApplicationRequest request) {
        application.setCompany(ValueUtils.clean(request.getCompany()));
        application.setRole(ValueUtils.clean(request.getRole()));
        application.setLocation(ValueUtils.clean(request.getLocation()));
        application.setStatus(ValueUtils.clean(request.getStatus(), "Applied"));
        application.setAppliedDate(request.getAppliedDate() == null ? LocalDate.now() : request.getAppliedDate());
        application.setReferralRequestId(request.getReferralRequestId());
        application.setSalaryRange(ValueUtils.clean(request.getSalaryRange()));
        application.setPostingUrl(ValueUtils.clean(request.getPostingUrl()));
        application.setNotes(ValueUtils.clean(request.getNotes()));
        application.setTags(ValueUtils.cleanList(request.getTags()));
    }
}
