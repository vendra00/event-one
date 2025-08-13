package com.t1tanic.eventone.service;

import com.t1tanic.eventone.model.dto.AvailabilitySlotDto;
import com.t1tanic.eventone.model.dto.request.CreateAvailabilityReq;
import com.t1tanic.eventone.model.dto.request.UpdateAvailabilityReq;

import java.time.LocalDateTime;
import java.util.List;

public interface AvailabilityService {
    List<AvailabilitySlotDto> listMine(Long userId, LocalDateTime from, LocalDateTime to);
    AvailabilitySlotDto create(Long userId, CreateAvailabilityReq req, boolean preventOverlap);
    AvailabilitySlotDto update(Long userId, Long slotId, UpdateAvailabilityReq req, boolean preventOverlap);
    void delete(Long userId, Long slotId);
}
