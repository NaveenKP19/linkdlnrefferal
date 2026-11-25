package com.personalproject.linkdlnrefferal.service;

import com.personalproject.linkdlnrefferal.dto.response.ActivityPointResponse;
import com.personalproject.linkdlnrefferal.dto.response.AnalyticsDashboardResponse;
import com.personalproject.linkdlnrefferal.dto.response.CompanyInsightResponse;
import com.personalproject.linkdlnrefferal.dto.response.DashboardStatResponse;
import com.personalproject.linkdlnrefferal.dto.response.FollowUpResponse;
import com.personalproject.linkdlnrefferal.dto.response.FunnelStepResponse;
import com.personalproject.linkdlnrefferal.dto.response.ReferralRequestResponse;
import com.personalproject.linkdlnrefferal.dto.response.StatusSliceResponse;

import java.util.List;

public interface AnalyticsService {

    AnalyticsDashboardResponse getDashboard(String range);

    List<DashboardStatResponse> getSummary(String range);

    List<ActivityPointResponse> getActivity(String range);

    List<StatusSliceResponse> getStatusDistribution(String range);

    List<CompanyInsightResponse> getCompanyInsights(String range);

    List<FunnelStepResponse> getFunnel(String range);

    List<ReferralRequestResponse> getRecentReferrals(String range);

    List<FollowUpResponse> getUpcomingFollowUps(String range);
}
