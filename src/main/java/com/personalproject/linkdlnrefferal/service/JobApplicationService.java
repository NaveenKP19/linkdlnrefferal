package com.personalproject.linkdlnrefferal.service;

import com.personalproject.linkdlnrefferal.dto.request.JobApplicationRequest;
import com.personalproject.linkdlnrefferal.dto.response.JobApplicationResponse;

public interface JobApplicationService extends OwnedCrudService<JobApplicationRequest, JobApplicationResponse> {

    JobApplicationResponse updateStatus(Long id, String status);
}
