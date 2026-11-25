package com.personalproject.linkdlnrefferal.service;

import com.personalproject.linkdlnrefferal.dto.request.CampaignMetricsRequest;
import com.personalproject.linkdlnrefferal.dto.request.CampaignRequest;
import com.personalproject.linkdlnrefferal.dto.response.CampaignResponse;

public interface CampaignService extends OwnedCrudService<CampaignRequest, CampaignResponse> {

    CampaignResponse updateStatus(Long id, String status);

    CampaignResponse updateMetrics(Long id, CampaignMetricsRequest request);
}
