package com.personalproject.linkdlnrefferal.service;

import com.personalproject.linkdlnrefferal.dto.request.FollowUpRequest;
import com.personalproject.linkdlnrefferal.dto.response.FollowUpResponse;

public interface FollowUpService extends OwnedCrudService<FollowUpRequest, FollowUpResponse> {

    FollowUpResponse complete(Long id);

    FollowUpResponse reopen(Long id);
}
