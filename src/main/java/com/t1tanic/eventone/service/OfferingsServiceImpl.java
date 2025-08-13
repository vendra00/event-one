package com.t1tanic.eventone.service;

import com.t1tanic.eventone.model.dto.OfferingDto;
import com.t1tanic.eventone.model.dto.request.CreateOfferingReq;
import com.t1tanic.eventone.model.dto.request.UpdateOfferingReq;
import com.t1tanic.eventone.model.mapper.OfferingMapper;
import com.t1tanic.eventone.repository.OfferingRepository;
import com.t1tanic.eventone.repository.ProviderProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OfferingsServiceImpl implements OfferingsService {

    private final OfferingRepository offerings;
    private final ProviderProfileRepository providers;
    private final OfferingMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Page<OfferingDto> listMine(Long userId, Pageable pageable) {
        var provider = providers.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("provider_profile_not_found_for_user:" + userId));
        return offerings.findByProviderId(provider.getId(), pageable).map(mapper::toDto);
    }

    @Override
    public OfferingDto create(Long userId, CreateOfferingReq req) {
        var provider = providers.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("provider_profile_not_found_for_user:" + userId));
        var entity = mapper.from(req, provider);
        var saved = offerings.save(entity);
        log.info("Created offering id={} providerId={}", saved.getId(), provider.getId());
        return mapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public OfferingDto getMine(Long userId, Long offeringId) {
        var provider = providers.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("provider_profile_not_found_for_user:" + userId));
        var o = offerings.findById(offeringId)
                .filter(it -> it.getProvider().getId().equals(provider.getId()))
                .orElseThrow(() -> new EntityNotFoundException("offering_not_found_or_not_owned:" + offeringId));
        return mapper.toDto(o);
    }

    @Override
    public OfferingDto update(Long userId, Long offeringId, UpdateOfferingReq req) {
        var provider = providers.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("provider_profile_not_found_for_user:" + userId));
        var o = offerings.findById(offeringId)
                .filter(it -> it.getProvider().getId().equals(provider.getId()))
                .orElseThrow(() -> new EntityNotFoundException("offering_not_found_or_not_owned:" + offeringId));
        mapper.apply(req, o);
        var saved = offerings.save(o);
        log.info("Updated offering id={}", saved.getId());
        return mapper.toDto(saved);
    }

    @Override
    public void delete(Long userId, Long offeringId) {
        var provider = providers.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("provider_profile_not_found_for_user:" + userId));
        var o = offerings.findById(offeringId)
                .filter(it -> it.getProvider().getId().equals(provider.getId()))
                .orElseThrow(() -> new EntityNotFoundException("offering_not_found_or_not_owned:" + offeringId));
        offerings.delete(o);
        log.info("Deleted offering id={}", o.getId());
    }
}
