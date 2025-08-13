package com.t1tanic.eventone.model.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record CreateAvailabilityReq(
        @NotNull LocalDateTime startsAt,
        @NotNull LocalDateTime endsAt,
        String note
) {}