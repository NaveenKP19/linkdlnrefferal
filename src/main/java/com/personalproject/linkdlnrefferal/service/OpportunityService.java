package com.personalproject.linkdlnrefferal.service;

import com.personalproject.linkdlnrefferal.dto.request.OpportunityRequest;
import com.personalproject.linkdlnrefferal.dto.response.OpportunityResponse;

public interface OpportunityService extends OwnedCrudService<OpportunityRequest, OpportunityResponse> {

    OpportunityResponse archive(Long id);
}
