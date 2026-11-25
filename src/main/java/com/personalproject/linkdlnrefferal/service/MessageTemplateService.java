package com.personalproject.linkdlnrefferal.service;

import com.personalproject.linkdlnrefferal.dto.request.MessageTemplateRequest;
import com.personalproject.linkdlnrefferal.dto.response.MessageTemplateResponse;

public interface MessageTemplateService extends OwnedCrudService<MessageTemplateRequest, MessageTemplateResponse> {

    MessageTemplateResponse setFavorite(Long id, Boolean favorite);
}
