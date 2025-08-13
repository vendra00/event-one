package com.t1tanic.eventone.service;

import com.t1tanic.eventone.model.AvailabilitySlot;
import com.t1tanic.eventone.model.dto.AvailabilitySlotDto;
import com.t1tanic.eventone.model.dto.request.CreateAvailabilityReq;
import com.t1tanic.eventone.model.dto.request.UpdateAvailabilityReq;
import com.t1tanic.eventone.model.mapper.AvailabilityMapper;
import com.t1tanic.eventone.repository.AvailabilitySlotRepository;
import com.t1tanic.eventone.repository.ProviderProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AvailabilityServiceImpl implements AvailabilityService {

    private final AvailabilitySlotRepository slots;
    private final ProviderProfileRepository providers;
    private final AvailabilityMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<AvailabilitySlotDto> listMine(Long userId, LocalDateTime from, LocalDateTime to) {
        var provider = providers.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("provider_profile_not_found_for_user:" + userId));

        List<AvailabilitySlot> list;
        if (from != null && to != null) {
            // overlap with [from,to]
            list = slots.findByProviderIdAndStartsAtLessThanEqualAndEndsAtGreaterThanEqual(
                    provider.getId(), to, from);
        } else {
            list = slots.findByProviderIdOrderByStartsAtAsc(provider.getId());
        }
        return list.stream().sorted(Comparator.comparing(AvailabilitySlot::getStartsAt))
                .map(mapper::toDto).toList();
    }

    @Override
    public AvailabilitySlotDto create(Long userId, CreateAvailabilityReq req, boolean preventOverlap) {
        validateRange(req.startsAt(), req.endsAt());
        var provider = providers.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("provider_profile_not_found_for_user:" + userId));

        if (preventOverlap && hasOverlap(provider.getId(), req.startsAt(), req.endsAt())) {
            throw new IllegalArgumentException("availability_overlap");
        }

        var entity = mapper.from(req, provider);
        var saved = slots.save(entity);
        log.info("Created availability id={} providerId={}", saved.getId(), provider.getId());
        return mapper.toDto(saved);
    }

    @Override
    public AvailabilitySlotDto update(Long userId, Long slotId, UpdateAvailabilityReq req, boolean preventOverlap) {
        var provider = providers.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("provider_profile_not_found_for_user:" + userId));

        var s = slots.findById(slotId)
                .filter(it -> it.getProvider().getId().equals(provider.getId()))
                .orElseThrow(() -> new EntityNotFoundException("availability_not_found_or_not_owned:" + slotId));

        mapper.apply(req, s);
        validateRange(s.getStartsAt(), s.getEndsAt());

        if (preventOverlap && hasOverlapExcluding(provider.getId(), s.getId(), s.getStartsAt(), s.getEndsAt())) {
            throw new IllegalArgumentException("availability_overlap");
        }

        var saved = slots.save(s);
        log.info("Updated availability id={}", saved.getId());
        return mapper.toDto(saved);
    }

    @Override
    public void delete(Long userId, Long slotId) {
        var provider = providers.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("provider_profile_not_found_for_user:" + userId));

        var s = slots.findById(slotId)
                .filter(it -> it.getProvider().getId().equals(provider.getId()))
                .orElseThrow(() -> new EntityNotFoundException("availability_not_found_or_not_owned:" + slotId));

        slots.delete(s);
        log.info("Deleted availability id={}", slotId);
    }

    // ---- helpers ----
    private static void validateRange(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null || !start.isBefore(end)) {
            throw new IllegalArgumentException("invalid_time_range");
        }
    }

    private boolean hasOverlap(Long providerId, LocalDateTime start, LocalDateTime end) {
        return !slots.findByProviderIdAndStartsAtLessThanEqualAndEndsAtGreaterThanEqual(providerId, end, start).isEmpty();
    }

    private boolean hasOverlapExcluding(Long providerId, Long excludeId, LocalDateTime start, LocalDateTime end) {
        return slots.findByProviderIdAndStartsAtLessThanEqualAndEndsAtGreaterThanEqual(providerId, end, start)
                .stream().anyMatch(s -> !s.getId().equals(excludeId));
    }
}
