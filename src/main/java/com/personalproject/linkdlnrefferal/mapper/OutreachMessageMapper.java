package com.personalproject.linkdlnrefferal.mapper;

import com.personalproject.linkdlnrefferal.dto.response.OutreachResponse;
import com.personalproject.linkdlnrefferal.entity.OutreachMessage;
import org.springframework.stereotype.Component;

@Component
public class OutreachMessageMapper {

    public OutreachResponse toResponse(OutreachMessage message) {
        OutreachResponse response = new OutreachResponse();
        response.setId(message.getId());
        response.setConnectionId(message.getConnectionId());
        response.setContactName(message.getContactName());
        response.setCompany(message.getCompany());
        response.setProfileUrl(message.getProfileUrl());
        response.setEmail(message.getEmail());
        response.setInviteStatus(message.getInviteStatus());
        response.setInviteNote(message.getInviteNote());
        response.setReferralMessage(message.getReferralMessage());
        response.setInviteSentAt(message.getInviteSentAt());
        response.setAcceptedAt(message.getAcceptedAt());
        response.setLastContactedAt(message.getLastContactedAt());
        response.setNextFollowUpAt(message.getNextFollowUpAt());
        response.setEmailSubject(message.getEmailSubject());
        response.setEmailBody(message.getEmailBody());
        response.setEmailStatus(message.getEmailStatus());
        response.setEmailSentAt(message.getEmailSentAt());
        response.setNotes(message.getNotes());
        response.setCreatedAt(message.getCreatedAt());
        response.setUpdatedAt(message.getUpdatedAt());
        return response;
    }
}
