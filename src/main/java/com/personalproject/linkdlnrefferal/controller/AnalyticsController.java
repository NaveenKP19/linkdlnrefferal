package com.personalproject.linkdlnrefferal.controller;

import com.personalproject.linkdlnrefferal.dto.response.ActivityPointResponse;
import com.personalproject.linkdlnrefferal.dto.response.AnalyticsDashboardResponse;
import com.personalproject.linkdlnrefferal.dto.response.CompanyInsightResponse;
import com.personalproject.linkdlnrefferal.dto.response.DashboardStatResponse;
import com.personalproject.linkdlnrefferal.dto.response.FollowUpResponse;
import com.personalproject.linkdlnrefferal.dto.response.FunnelStepResponse;
import com.personalproject.linkdlnrefferal.dto.response.ReferralRequestResponse;
import com.personalproject.linkdlnrefferal.dto.response.StatusSliceResponse;
import com.personalproject.linkdlnrefferal.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/dashboard")
    public ResponseEntity<AnalyticsDashboardResponse> getDashboard(@RequestParam(required = false) String range) {
        return ResponseEntity.ok(analyticsService.getDashboard(range));
    }

    @GetMapping("/summary")
    public ResponseEntity<List<DashboardStatResponse>> getSummary(@RequestParam(required = false) String range) {
        return ResponseEntity.ok(analyticsService.getSummary(range));
    }

    @GetMapping("/activity")
    public ResponseEntity<List<ActivityPointResponse>> getActivity(@RequestParam(required = false) String range) {
        return ResponseEntity.ok(analyticsService.getActivity(range));
    }

    @GetMapping("/status-distribution")
    public ResponseEntity<List<StatusSliceResponse>> getStatusDistribution(@RequestParam(required = false) String range) {
        return ResponseEntity.ok(analyticsService.getStatusDistribution(range));
    }

    @GetMapping("/company-insights")
    public ResponseEntity<List<CompanyInsightResponse>> getCompanyInsights(@RequestParam(required = false) String range) {
        return ResponseEntity.ok(analyticsService.getCompanyInsights(range));
    }

    @GetMapping("/funnel")
    public ResponseEntity<List<FunnelStepResponse>> getFunnel(@RequestParam(required = false) String range) {
        return ResponseEntity.ok(analyticsService.getFunnel(range));
    }

    @GetMapping("/recent-referrals")
    public ResponseEntity<List<ReferralRequestResponse>> getRecentReferrals(@RequestParam(required = false) String range) {
        return ResponseEntity.ok(analyticsService.getRecentReferrals(range));
    }

    @GetMapping("/upcoming-follow-ups")
    public ResponseEntity<List<FollowUpResponse>> getUpcomingFollowUps(@RequestParam(required = false) String range) {
        return ResponseEntity.ok(analyticsService.getUpcomingFollowUps(range));
    }
}
