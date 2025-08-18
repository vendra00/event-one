package com.t1tanic.eventone.service;

import com.t1tanic.eventone.model.*;
import com.t1tanic.eventone.model.dto.EventRequestDto;
import com.t1tanic.eventone.model.dto.request.CreateEventRequestReq;
import com.t1tanic.eventone.model.enums.EventRequestStatus;
import com.t1tanic.eventone.model.mapper.EventRequestMapper;
import com.t1tanic.eventone.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public
class EventRequestServiceImpl implements EventRequestService {

    private final EventRequestRepository eventRequests;
    private final AppUserRepository users;
    private final ProviderProfileRepository providers;
    private final OfferingRepository offeringRepo;
    private final EventRequestMapper mapper;

    @Override
    public EventRequestDto create(Long consumerUserId, CreateEventRequestReq req) {
        if (!req.startsAt().isBefore(req.endsAt()))
            throw new IllegalArgumentException("invalid_time_range");

        var consumer = users.findById(consumerUserId)
                .orElseThrow(() -> new EntityNotFoundException("user_not_found:" + consumerUserId));

        ProviderProfile provider = null;
        if (req.providerId() != null) {
            provider = providers.findById(req.providerId())
                    .orElseThrow(() -> new EntityNotFoundException("provider_not_found:" + req.providerId()));
        }

        Offering offering = null;
        if (req.offeringId() != null) {
            offering = offeringRepo.findById(req.offeringId())
                    .orElseThrow(() -> new EntityNotFoundException("offering_not_found:" + req.offeringId()));
            if (provider == null) provider = offering.getProvider();
        }

        var entity = mapper.from(req, consumer, provider, offering);
        var saved = eventRequests.save(entity);
        log.info("Created event request id={} by userId={}", saved.getId(), consumerUserId);
        return mapper.toDto(saved);
    }

    @Override @Transactional(readOnly = true)
    public Page<EventRequestDto> listMine(Long consumerUserId, Pageable pageable) {
        return eventRequests.findByConsumerId(consumerUserId, pageable).map(mapper::toDto);
    }

    @Override @Transactional(readOnly = true)
    public EventRequestDto getMine(Long consumerUserId, Long id) {
        var er = eventRequests.findById(id)
                .filter(e -> e.getConsumer() != null && e.getConsumer().getId().equals(consumerUserId))
                .orElseThrow(() -> new EntityNotFoundException("request_not_found_or_not_owned:" + id));
        return mapper.toDto(er);
    }

    @Override
    public EventRequestDto cancelMine(Long consumerUserId, Long id) {
        var er = eventRequests.findById(id)
                .filter(e -> e.getConsumer() != null && e.getConsumer().getId().equals(consumerUserId))
                .orElseThrow(() -> new EntityNotFoundException("request_not_found_or_not_owned:" + id));
        if (er.getStatus() != EventRequestStatus.OPEN)
            throw new IllegalStateException("only_open_can_be_cancelled");
        er.setStatus(EventRequestStatus.CANCELED);
        return mapper.toDto(eventRequests.save(er));
    }

    // Providers

    @Override
    @Transactional(readOnly = true)
    public Page<EventRequestDto> listForProvider(Long providerUserId, boolean includeUntargetedOpenNearby, Pageable pageable) {
        var provider = providers.findByUserId(providerUserId)
                .orElseThrow(() -> new EntityNotFoundException("provider_profile_not_found_for_user:" + providerUserId));

        if (!includeUntargetedOpenNearby) {
            return eventRequests.findByProviderId(provider.getId(), pageable).map(mapper::toDto);
        }

        var loc = provider.getLocation();
        if (loc == null) {
            // Geo-only mode: if provider has no normalized location, we return empty.
            return Page.empty(pageable);
        }

        Page<EventRequest> page;
        if (loc.getMunicipality() != null && loc.getMunicipality().getCode() != null && !loc.getMunicipality().getCode().isBlank()) {
            page = eventRequests.findByStatusAndProviderIsNullAndLocation_Municipality_CodeIgnoreCase(
                    EventRequestStatus.OPEN, loc.getMunicipality().getCode(), pageable);
        } else if (loc.getProvince() != null && loc.getProvince().getCode() != null && !loc.getProvince().getCode().isBlank()) {
            page = eventRequests.findByStatusAndProviderIsNullAndLocation_Province_CodeIgnoreCase(
                    EventRequestStatus.OPEN, loc.getProvince().getCode(), pageable);
        } else if (loc.getCommunity() != null && loc.getCommunity().getCode() != null && !loc.getCommunity().getCode().isBlank()) {
            page = eventRequests.findByStatusAndProviderIsNullAndLocation_Community_CodeIgnoreCase(
                    EventRequestStatus.OPEN, loc.getCommunity().getCode(), pageable);
        } else {
            // No usable geo codes on provider -> empty
            return Page.empty(pageable);
        }

        return page.map(mapper::toDto);
    }

    @Override @Transactional(readOnly = true)
    public EventRequestDto getForProvider(Long providerUserId, Long id) {
        var provider = providers.findByUserId(providerUserId)
                .orElseThrow(() -> new EntityNotFoundException("provider_profile_not_found_for_user:" + providerUserId));
        var er = eventRequests.findById(id)
                .filter(e -> e.getProvider() != null && e.getProvider().getId().equals(provider.getId()))
                .orElseThrow(() -> new EntityNotFoundException("request_not_found_or_not_targeted:" + id));
        return mapper.toDto(er);
    }

}
