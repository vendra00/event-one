package com.t1tanic.eventone.model.dto;

import com.t1tanic.eventone.model.enums.BookingStatus;
import java.time.LocalDateTime;

public record BookingDto(
        Long id,
        Long proposalId,
        Long requestId,
        Long providerId,
        Long consumerId,
        LocalDateTime eventDate,
        Integer headcount,
        BookingStatus status
) {}
