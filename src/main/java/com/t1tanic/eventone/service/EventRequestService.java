package com.t1tanic.eventone.service;

import com.t1tanic.eventone.model.dto.EventRequestDto;
import com.t1tanic.eventone.model.dto.request.CreateEventRequestReq;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventRequestService {
    EventRequestDto create(Long consumerUserId, CreateEventRequestReq req);
    Page<EventRequestDto> listMine(Long consumerUserId, Pageable pageable);
    EventRequestDto getMine(Long consumerUserId, Long id);
    EventRequestDto cancelMine(Long consumerUserId, Long id);

    Page<EventRequestDto> listForProvider(Long providerUserId, boolean includeUntargetedOpenNearby, Pageable pageable);
    EventRequestDto getForProvider(Long providerUserId, Long id);
}
