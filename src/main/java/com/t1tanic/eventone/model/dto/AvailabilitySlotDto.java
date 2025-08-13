package com.t1tanic.eventone.model.dto;

import java.time.LocalDateTime;

public record AvailabilitySlotDto(
        Long id,
        Long providerId,
        LocalDateTime startsAt,
        LocalDateTime endsAt,
        String note
) {}
