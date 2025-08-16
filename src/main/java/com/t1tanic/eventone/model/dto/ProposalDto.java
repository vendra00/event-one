package com.t1tanic.eventone.model.dto;

import com.t1tanic.eventone.model.enums.ProposalStatus;
import java.time.LocalDateTime;

public record ProposalDto(
        Long id,
        Long requestId,
        Long providerId,
        Long offeringId,
        Integer priceCents,
        String currency,
        String message,
        ProposalStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
