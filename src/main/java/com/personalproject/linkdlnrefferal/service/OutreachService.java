package com.personalproject.linkdlnrefferal.service;

import com.personalproject.linkdlnrefferal.dto.request.OutreachRequest;
import com.personalproject.linkdlnrefferal.dto.response.DashboardStatResponse;
import com.personalproject.linkdlnrefferal.dto.response.OutreachResponse;

import java.util.List;

public interface OutreachService extends OwnedCrudService<OutreachRequest, OutreachResponse> {

    List<DashboardStatResponse> getSummary(String range);

    OutreachResponse markInviteSent(Long id);

    OutreachResponse markAccepted(Long id);

    OutreachResponse markDeclined(Long id);

    OutreachResponse markContacted(Long id);
}
