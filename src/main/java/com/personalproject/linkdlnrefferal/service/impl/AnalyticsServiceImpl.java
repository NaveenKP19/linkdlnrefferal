package com.personalproject.linkdlnrefferal.service.impl;

import com.personalproject.linkdlnrefferal.dto.response.ActivityPointResponse;
import com.personalproject.linkdlnrefferal.dto.response.AnalyticsDashboardResponse;
import com.personalproject.linkdlnrefferal.dto.response.CompanyInsightResponse;
import com.personalproject.linkdlnrefferal.dto.response.DashboardStatResponse;
import com.personalproject.linkdlnrefferal.dto.response.FollowUpResponse;
import com.personalproject.linkdlnrefferal.dto.response.FunnelStepResponse;
import com.personalproject.linkdlnrefferal.dto.response.ReferralRequestResponse;
import com.personalproject.linkdlnrefferal.dto.response.StatusSliceResponse;
import com.personalproject.linkdlnrefferal.entity.FollowUp;
import com.personalproject.linkdlnrefferal.entity.ReferralRequest;
import com.personalproject.linkdlnrefferal.mapper.FollowUpMapper;
import com.personalproject.linkdlnrefferal.mapper.ReferralRequestMapper;
import com.personalproject.linkdlnrefferal.repository.CampaignRepository;
import com.personalproject.linkdlnrefferal.repository.ConnectionRepository;
import com.personalproject.linkdlnrefferal.repository.FollowUpRepository;
import com.personalproject.linkdlnrefferal.repository.JobApplicationRepository;
import com.personalproject.linkdlnrefferal.repository.OpportunityRepository;
import com.personalproject.linkdlnrefferal.repository.ReferralRequestRepository;
import com.personalproject.linkdlnrefferal.security.AuthenticatedUserService;
import com.personalproject.linkdlnrefferal.service.AnalyticsService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class AnalyticsServiceImpl implements AnalyticsService {

    private static final List<String> STATUS_COLORS =
            List.of("#0d6efd", "#198754", "#ffc107", "#fd7e14", "#dc3545", "#6f42c1", "#6c757d");

    private final ConnectionRepository connectionRepository;
    private final OpportunityRepository opportunityRepository;
    private final ReferralRequestRepository referralRequestRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final CampaignRepository campaignRepository;
    private final FollowUpRepository followUpRepository;
    private final ReferralRequestMapper referralRequestMapper;
    private final FollowUpMapper followUpMapper;
    private final AuthenticatedUserService authenticatedUserService;

    @Override
    public AnalyticsDashboardResponse getDashboard(String range) {
        return AnalyticsDashboardResponse.builder()
                .stats(getSummary(range))
                .activity(getActivity(range))
                .statusDistribution(getStatusDistribution(range))
                .companyInsights(getCompanyInsights(range))
                .funnel(getFunnel(range))
                .recentReferrals(getRecentReferrals(range))
                .upcomingFollowUps(getUpcomingFollowUps(range))
                .build();
    }

    @Override
    public List<DashboardStatResponse> getSummary(String range) {
        Long userId = currentUserId();
        List<ReferralRequest> referrals = inRange(referralRequestRepository.findAllByUserIdOrderByCreatedAtDesc(userId), range);
        long sent = referrals.stream().filter(referral -> isSent(referral.getStatus())).count();
        long accepted = referrals.stream().filter(referral -> "Accepted".equalsIgnoreCase(referral.getStatus())).count();
        long activeApplications = jobApplicationRepository.findAllByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .filter(application -> !List.of("Rejected", "Withdrawn", "Closed").contains(application.getStatus()))
                .count();
        long pendingFollowUps = followUpRepository.findAllByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .filter(followUp -> followUp.getCompletedDate() == null)
                .count();

        return List.of(
                stat("connections", "Connections", String.valueOf(connectionRepository.findAllByUserIdOrderByCreatedAtDesc(userId).size()), "Saved professional contacts", "primary", "bi-people"),
                stat("opportunities", "Opportunities", String.valueOf(opportunityRepository.findAllByUserIdOrderByCreatedAtDesc(userId).size()), "Tracked roles and companies", "info", "bi-briefcase"),
                stat("referrals", "Referral Requests", String.valueOf(referrals.size()), "Requests in the selected range", "success", "bi-send"),
                stat("sent", "Sent", String.valueOf(sent), "Referral requests sent", "warning", "bi-check2-circle"),
                stat("accepted", "Accepted", String.valueOf(accepted), "Accepted referrals", "success", "bi-trophy"),
                stat("applications", "Active Applications", String.valueOf(activeApplications), "Applications still in progress", "primary", "bi-file-earmark-text"),
                stat("followUps", "Open Follow-ups", String.valueOf(pendingFollowUps), "Follow-ups still to complete", "danger", "bi-clock-history")
        );
    }

    @Override
    public List<ActivityPointResponse> getActivity(String range) {
        Long userId = currentUserId();
        int days = rangeToDays(range);
        LocalDate today = LocalDate.now();
        LocalDate start = today.minusDays(days - 1L);

        List<ReferralRequest> referrals = referralRequestRepository.findAllByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .filter(referral -> referral.getRequestDate() != null && !referral.getRequestDate().isBefore(start))
                .toList();

        List<ActivityPointResponse> points = new ArrayList<>();
        for (int offset = days - 1; offset >= 0; offset--) {
            LocalDate date = today.minusDays(offset);
            List<ReferralRequest> daily = referrals.stream()
                    .filter(referral -> date.equals(referral.getRequestDate()))
                    .toList();

            long responses = daily.stream()
                    .filter(referral -> List.of("Accepted", "Rejected", "No Response").contains(referral.getStatus()))
                    .count();
            long successful = daily.stream()
                    .filter(referral -> isSent(referral.getStatus()) || "Accepted".equalsIgnoreCase(referral.getStatus()))
                    .count();

            points.add(ActivityPointResponse.builder()
                    .date(date.toString())
                    .label(date.format(java.time.format.DateTimeFormatter.ofPattern("MMM d")))
                    .requests((int) daily.size())
                    .responses((int) responses)
                    .referrals((int) successful)
                    .build());
        }

        return points;
    }

    @Override
    public List<StatusSliceResponse> getStatusDistribution(String range) {
        Map<String, Long> counts = new LinkedHashMap<>();
        for (ReferralRequest referral : inRange(referralRequestRepository.findAllByUserIdOrderByCreatedAtDesc(currentUserId()), range)) {
            String status = referral.getStatus() == null || referral.getStatus().isBlank() ? "Draft" : referral.getStatus();
            counts.merge(status, 1L, Long::sum);
        }

        List<StatusSliceResponse> slices = new ArrayList<>();
        int index = 0;
        for (Map.Entry<String, Long> entry : counts.entrySet()) {
            slices.add(StatusSliceResponse.builder()
                    .label(entry.getKey())
                    .value(entry.getValue().intValue())
                    .color(STATUS_COLORS.get(index % STATUS_COLORS.size()))
                    .build());
            index++;
        }

        return slices;
    }

    @Override
    public List<CompanyInsightResponse> getCompanyInsights(String range) {
        Long userId = currentUserId();
        Map<String, int[]> insights = new LinkedHashMap<>();

        opportunityRepository.findAllByUserIdOrderByCreatedAtDesc(userId)
                .forEach(opportunity -> increment(insights, opportunity.getCompany(), 0));
        inRange(referralRequestRepository.findAllByUserIdOrderByCreatedAtDesc(userId), range)
                .forEach(referral -> increment(insights, referral.getCompany(), 1));
        jobApplicationRepository.findAllByUserIdOrderByCreatedAtDesc(userId)
                .forEach(application -> increment(insights, application.getCompany(), 2));

        return insights.entrySet().stream()
                .sorted(Map.Entry.<String, int[]>comparingByValue(Comparator.comparingInt(values -> values[1])).reversed())
                .map(entry -> CompanyInsightResponse.builder()
                        .company(entry.getKey())
                        .opportunities(entry.getValue()[0])
                        .requests(entry.getValue()[1])
                        .applications(entry.getValue()[2])
                        .build())
                .toList();
    }

    @Override
    public List<FunnelStepResponse> getFunnel(String range) {
        Long userId = currentUserId();
        List<ReferralRequest> referrals = inRange(referralRequestRepository.findAllByUserIdOrderByCreatedAtDesc(userId), range);

        int connections = connectionRepository.findAllByUserIdOrderByCreatedAtDesc(userId).size();
        int opportunities = opportunityRepository.findAllByUserIdOrderByCreatedAtDesc(userId).size();
        int sent = (int) referrals.stream().filter(referral -> isSent(referral.getStatus())).count();
        int applications = jobApplicationRepository.findAllByUserIdOrderByCreatedAtDesc(userId).size();
        int accepted = (int) referrals.stream().filter(referral -> "Accepted".equalsIgnoreCase(referral.getStatus())).count();

        List<FunnelStepResponse> steps = new ArrayList<>();
        steps.add(step("Connections", connections, connections));
        steps.add(step("Opportunities", opportunities, connections));
        steps.add(step("Referrals sent", sent, connections));
        steps.add(step("Applications", applications, connections));
        steps.add(step("Accepted", accepted, connections));
        return steps;
    }

    @Override
    public List<ReferralRequestResponse> getRecentReferrals(String range) {
        return inRange(referralRequestRepository.findAllByUserIdOrderByCreatedAtDesc(currentUserId()), range)
                .stream()
                .sorted(Comparator.comparing(ReferralRequest::getUpdatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .map(referralRequestMapper::toResponse)
                .toList();
    }

    @Override
    public List<FollowUpResponse> getUpcomingFollowUps(String range) {
        LocalDate today = LocalDate.now();

        return followUpRepository.findAllByUserIdOrderByCreatedAtDesc(currentUserId())
                .stream()
                .filter(followUp -> followUp.getCompletedDate() == null)
                .filter(followUp -> followUp.getDueDate() == null || !followUp.getDueDate().isBefore(today.minusDays(rangeToDays(range))))
                .sorted(Comparator.comparing(FollowUp::getDueDate, Comparator.nullsLast(Comparator.naturalOrder())))
                .limit(5)
                .map(followUpMapper::toResponse)
                .toList();
    }

    private DashboardStatResponse stat(String key, String label, String value, String description, String tone, String icon) {
        return DashboardStatResponse.builder()
                .key(key)
                .label(label)
                .value(value)
                .description(description)
                .tone(tone)
                .icon(icon)
                .build();
    }

    private FunnelStepResponse step(String label, int value, int maximum) {
        int width = maximum == 0 ? 0 : Math.round(value * 100f / maximum);
        return FunnelStepResponse.builder()
                .label(label)
                .value(value)
                .width(width)
                .build();
    }

    private void increment(Map<String, int[]> insights, String company, int index) {
        String key = company == null || company.isBlank() ? "Other" : company.trim();
        int[] values = insights.computeIfAbsent(key, ignored -> new int[3]);
        values[index]++;
    }

    private boolean isSent(String status) {
        return status != null && List.of("Sent", "Pending", "Accepted", "Rejected", "No Response")
                .contains(status);
    }

    private List<ReferralRequest> inRange(List<ReferralRequest> referrals, String range) {
        LocalDate cutoff = LocalDate.now().minusDays(rangeToDays(range) - 1L);
        return referrals.stream()
                .filter(referral -> referral.getRequestDate() == null || !referral.getRequestDate().isBefore(cutoff))
                .toList();
    }

    private int rangeToDays(String range) {
        if (range == null || range.isBlank()) {
            return 30;
        }

        String normalized = range.trim().toLowerCase(Locale.ROOT);
        return switch (normalized) {
            case "7d" -> 7;
            case "30d" -> 30;
            case "3m" -> 90;
            case "6m" -> 180;
            case "ytd" -> Math.max(1, LocalDate.now().getDayOfYear());
            default -> parseDays(normalized);
        };
    }

    private int parseDays(String value) {
        try {
            String digits = value.endsWith("d") ? value.substring(0, value.length() - 1) : value;
            int days = Integer.parseInt(digits);
            return Math.min(365, Math.max(1, days));
        } catch (NumberFormatException exception) {
            return 30;
        }
    }

    private Long currentUserId() {
        return authenticatedUserService.getCurrentUserAccount().getId();
    }
}
