package com.personalproject.linkdlnrefferal.service.impl;

import com.personalproject.linkdlnrefferal.dto.request.OutreachRequest;
import com.personalproject.linkdlnrefferal.dto.response.DashboardStatResponse;
import com.personalproject.linkdlnrefferal.dto.response.OutreachResponse;
import com.personalproject.linkdlnrefferal.entity.OutreachMessage;
import com.personalproject.linkdlnrefferal.exception.ResourceNotFoundException;
import com.personalproject.linkdlnrefferal.mapper.OutreachMessageMapper;
import com.personalproject.linkdlnrefferal.repository.OutreachMessageRepository;
import com.personalproject.linkdlnrefferal.security.AuthenticatedUserService;
import com.personalproject.linkdlnrefferal.service.OutreachService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OutreachServiceImpl implements OutreachService {

    private static final String NOT_SENT = "Not Sent";
    private static final String SENT = "Sent";
    private static final String ACCEPTED = "Accepted";
    private static final String DECLINED = "Declined";
    private static final String EMAIL_SENT = "Sent";

    private final OutreachMessageRepository outreachMessageRepository;
    private final OutreachMessageMapper outreachMessageMapper;
    private final AuthenticatedUserService authenticatedUserService;

    @Override
    public OutreachResponse create(OutreachRequest request) {
        OutreachMessage message = new OutreachMessage();
        message.setUserId(currentUserId());
        applyRequest(message, request);
        return outreachMessageMapper.toResponse(outreachMessageRepository.save(message));
    }

    @Override
    public List<OutreachResponse> getAll() {
        return outreachMessageRepository.findAllByUserIdOrderByCreatedAtDesc(currentUserId())
                .stream()
                .map(outreachMessageMapper::toResponse)
                .toList();
    }

    @Override
    public OutreachResponse getById(Long id) {
        return outreachMessageMapper.toResponse(getOwned(id));
    }

    @Override
    public OutreachResponse update(Long id, OutreachRequest request) {
        OutreachMessage message = getOwned(id);
        applyRequest(message, request);
        return outreachMessageMapper.toResponse(outreachMessageRepository.save(message));
    }

    @Override
    public void delete(Long id) {
        outreachMessageRepository.delete(getOwned(id));
    }

    @Override
    public List<DashboardStatResponse> getSummary(String range) {
        List<OutreachMessage> messages = outreachMessageRepository
                .findAllByUserIdOrderByCreatedAtDesc(currentUserId());

        long sent = messages.stream()
                .filter(message -> SENT.equalsIgnoreCase(message.getInviteStatus()))
                .count();
        long accepted = messages.stream()
                .filter(message -> ACCEPTED.equalsIgnoreCase(message.getInviteStatus()))
                .count();
        long notSent = messages.stream()
                .filter(message -> message.getInviteStatus() == null
                        || NOT_SENT.equalsIgnoreCase(message.getInviteStatus()))
                .count();
        long emailsSent = messages.stream()
                .filter(message -> EMAIL_SENT.equalsIgnoreCase(message.getEmailStatus()))
                .count();

        return List.of(
                stat("all", "LinkedIn Outreach", String.valueOf(messages.size()), "Tracked outreach records", "primary", "bi-people"),
                stat(NOT_SENT, "Not Sent", String.valueOf(notSent), "Invites ready to send", "secondary", "bi-pencil-square"),
                stat(SENT, "Pending", String.valueOf(sent), "Invites sent and awaiting response", "warning", "bi-hourglass-split"),
                stat(ACCEPTED, "Accepted", String.valueOf(accepted), "Connections who accepted", "success", "bi-check-circle"),
                stat("emailSent", "Emails Sent", String.valueOf(emailsSent), "Referral emails marked sent", "info", "bi-envelope-check")
        );
    }

    @Override
    public OutreachResponse markInviteSent(Long id) {
        OutreachMessage message = getOwned(id);
        message.setInviteStatus(SENT);
        message.setInviteSentAt(LocalDateTime.now());
        message.setLastContactedAt(LocalDateTime.now());
        return outreachMessageMapper.toResponse(outreachMessageRepository.save(message));
    }

    @Override
    public OutreachResponse markAccepted(Long id) {
        OutreachMessage message = getOwned(id);
        message.setInviteStatus(ACCEPTED);
        message.setAcceptedAt(LocalDateTime.now());
        message.setLastContactedAt(LocalDateTime.now());
        return outreachMessageMapper.toResponse(outreachMessageRepository.save(message));
    }

    @Override
    public OutreachResponse markDeclined(Long id) {
        OutreachMessage message = getOwned(id);
        message.setInviteStatus(DECLINED);
        message.setLastContactedAt(LocalDateTime.now());
        return outreachMessageMapper.toResponse(outreachMessageRepository.save(message));
    }

    @Override
    public OutreachResponse markContacted(Long id) {
        OutreachMessage message = getOwned(id);
        message.setLastContactedAt(LocalDateTime.now());
        message.setNextFollowUpAt(LocalDateTime.now().plusDays(7));
        return outreachMessageMapper.toResponse(outreachMessageRepository.save(message));
    }

    private OutreachMessage getOwned(Long id) {
        return outreachMessageRepository.findByIdAndUserId(id, currentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Outreach message not found with ID: " + id));
    }

    private Long currentUserId() {
        return authenticatedUserService.getCurrentUserAccount().getId();
    }

    private void applyRequest(OutreachMessage message, OutreachRequest request) {
        message.setConnectionId(request.getConnectionId());
        message.setContactName(value(request.getContactName(), message.getContactName()));
        message.setCompany(value(request.getCompany(), message.getCompany()));
        message.setProfileUrl(value(request.getProfileUrl(), message.getProfileUrl()));
        message.setEmail(value(request.getEmail(), message.getEmail()));
        message.setInviteStatus(valueOr(request.getInviteStatus(), message.getInviteStatus(), NOT_SENT));
        message.setInviteNote(value(request.getInviteNote(), message.getInviteNote()));
        message.setReferralMessage(value(request.getReferralMessage(), message.getReferralMessage()));
        message.setInviteSentAt(value(request.getInviteSentAt(), message.getInviteSentAt()));
        message.setAcceptedAt(value(request.getAcceptedAt(), message.getAcceptedAt()));
        message.setLastContactedAt(value(request.getLastContactedAt(), message.getLastContactedAt()));
        message.setNextFollowUpAt(value(request.getNextFollowUpAt(), message.getNextFollowUpAt()));
        message.setEmailSubject(value(request.getEmailSubject(), message.getEmailSubject()));
        message.setEmailBody(value(request.getEmailBody(), message.getEmailBody()));
        message.setEmailStatus(valueOr(request.getEmailStatus(), message.getEmailStatus(), "Draft"));
        message.setEmailSentAt(value(request.getEmailSentAt(), message.getEmailSentAt()));
        message.setNotes(value(request.getNotes(), message.getNotes()));
    }

    private <T> T value(T provided, T current) {
        return provided == null ? current : provided;
    }

    private String valueOr(String provided, String current, String fallback) {
        String resolved = value(provided, current);
        return resolved == null || resolved.isBlank() ? fallback : resolved;
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
}
