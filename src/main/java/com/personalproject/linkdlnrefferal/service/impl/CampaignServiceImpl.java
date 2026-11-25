package com.personalproject.linkdlnrefferal.service.impl;

import com.personalproject.linkdlnrefferal.dto.request.CampaignMetricsRequest;
import com.personalproject.linkdlnrefferal.dto.request.CampaignRequest;
import com.personalproject.linkdlnrefferal.dto.response.CampaignResponse;
import com.personalproject.linkdlnrefferal.entity.Campaign;
import com.personalproject.linkdlnrefferal.exception.ResourceNotFoundException;
import com.personalproject.linkdlnrefferal.mapper.CampaignMapper;
import com.personalproject.linkdlnrefferal.repository.CampaignRepository;
import com.personalproject.linkdlnrefferal.security.AuthenticatedUserService;
import com.personalproject.linkdlnrefferal.service.CampaignService;
import com.personalproject.linkdlnrefferal.util.ValueUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CampaignServiceImpl implements CampaignService {

    private final CampaignRepository campaignRepository;
    private final CampaignMapper campaignMapper;
    private final AuthenticatedUserService authenticatedUserService;

    @Override
    public CampaignResponse create(CampaignRequest request) {
        Campaign campaign = new Campaign();
        campaign.setUserId(currentUserId());
        applyRequest(campaign, request);
        return campaignMapper.toResponse(campaignRepository.save(campaign));
    }

    @Override
    public List<CampaignResponse> getAll() {
        return campaignRepository.findAllByUserIdOrderByCreatedAtDesc(currentUserId())
                .stream()
                .map(campaignMapper::toResponse)
                .toList();
    }

    @Override
    public CampaignResponse getById(Long id) {
        return campaignMapper.toResponse(getOwned(id));
    }

    @Override
    public CampaignResponse update(Long id, CampaignRequest request) {
        Campaign campaign = getOwned(id);
        applyRequest(campaign, request);
        return campaignMapper.toResponse(campaignRepository.save(campaign));
    }

    @Override
    public void delete(Long id) {
        campaignRepository.delete(getOwned(id));
    }

    @Override
    public CampaignResponse updateStatus(Long id, String status) {
        Campaign campaign = getOwned(id);
        campaign.setStatus(ValueUtils.clean(status, "Draft"));
        return campaignMapper.toResponse(campaignRepository.save(campaign));
    }

    @Override
    public CampaignResponse updateMetrics(Long id, CampaignMetricsRequest request) {
        Campaign campaign = getOwned(id);
        if (request.getSentCount() != null) {
            campaign.setSentCount(Math.max(0, request.getSentCount()));
        }
        if (request.getResponseCount() != null) {
            campaign.setResponseCount(Math.max(0, request.getResponseCount()));
        }
        return campaignMapper.toResponse(campaignRepository.save(campaign));
    }

    private Campaign getOwned(Long id) {
        return campaignRepository.findByIdAndUserId(id, currentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with ID: " + id));
    }

    private Long currentUserId() {
        return authenticatedUserService.getCurrentUserAccount().getId();
    }

    private void applyRequest(Campaign campaign, CampaignRequest request) {
        campaign.setName(ValueUtils.clean(request.getName()));
        campaign.setDescription(ValueUtils.clean(request.getDescription()));
        campaign.setStatus(ValueUtils.clean(request.getStatus(), "Draft"));
        campaign.setChannel(ValueUtils.clean(request.getChannel(), "Manual"));
        campaign.setAudience(ValueUtils.clean(request.getAudience()));
        campaign.setTemplateId(request.getTemplateId());
        campaign.setScheduledDate(request.getScheduledDate());
        campaign.setStartDate(request.getStartDate());
        campaign.setCompletedDate(request.getCompletedDate());
        campaign.setSentCount(request.getSentCount() == null ? 0 : Math.max(0, request.getSentCount()));
        campaign.setResponseCount(request.getResponseCount() == null ? 0 : Math.max(0, request.getResponseCount()));
        campaign.setTags(ValueUtils.cleanList(request.getTags()));
        campaign.setNotes(ValueUtils.clean(request.getNotes()));
    }
}
