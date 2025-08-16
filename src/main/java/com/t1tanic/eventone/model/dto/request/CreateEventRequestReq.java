package com.t1tanic.eventone.model.dto.request;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record CreateEventRequestReq(
        @NotBlank @Size(max=200) String title,
        Long offeringId,       // optional
        Long providerId,       // optional
        @NotNull LocalDateTime startsAt,
        @NotNull LocalDateTime endsAt,
        @NotNull @Min(1) Integer guests,
        @Size(max=120) String city,
        @Size(max=120) String region,
        @Size(max=400) String cuisines,
        @Size(max=400) String services,
        @Min(0) Integer budgetCents,
        @Size(min=3,max=3) String currency,
        @Size(max=2000) String notes
) {}
