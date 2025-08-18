package com.t1tanic.eventone.model.dto;

import com.t1tanic.eventone.model.enums.EventRequestStatus;
import java.time.LocalDateTime;

public record EventRequestDto(
        Long id,
        Long consumerId,
        Long providerId,
        Long offeringId,
        String title,
        LocalDateTime startsAt,
        LocalDateTime endsAt,
        Integer guests,
        GeoLocationDto location,   // <-- normalized geo
        String cuisines,
        String services,
        Integer budgetCents,
        String currency,
        String notes,
        EventRequestStatus status,
        LocalDateTime createdAt
) {}