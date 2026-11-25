package com.personalproject.linkdlnrefferal.service;

import com.personalproject.linkdlnrefferal.dto.request.ReferralRequestRequest;
import com.personalproject.linkdlnrefferal.dto.response.ReferralRequestResponse;

public interface ReferralRequestService extends OwnedCrudService<ReferralRequestRequest, ReferralRequestResponse> {

    ReferralRequestResponse updateStatus(Long id, String status);
}
