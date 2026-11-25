package com.personalproject.linkdlnrefferal.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AnalyticsDashboardResponse {

    private List<DashboardStatResponse> stats;
    private List<ActivityPointResponse> activity;
    private List<StatusSliceResponse> statusDistribution;
    private List<CompanyInsightResponse> companyInsights;
    private List<FunnelStepResponse> funnel;
    private List<ReferralRequestResponse> recentReferrals;
    private List<FollowUpResponse> upcomingFollowUps;
}
