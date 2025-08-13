package com.t1tanic.eventone.model.dto.request;

import java.time.LocalDateTime;

public record UpdateAvailabilityReq(
        LocalDateTime startsAt,
        LocalDateTime endsAt,
        String note
) {}