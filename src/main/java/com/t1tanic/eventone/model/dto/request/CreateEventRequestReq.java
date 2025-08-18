package com.t1tanic.eventone.model.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record CreateEventRequestReq(
        @NotBlank @Size(max = 200) String title,
        Long offeringId,
        Long providerId,
        @NotNull LocalDateTime startsAt,
        @NotNull LocalDateTime endsAt,
        @NotNull @Min(1) Integer guests,
        @Valid GeoLocationInput geo,
        @Size(max = 400) String cuisines,
        @Size(max = 400) String services,
        @Min(0) Integer budgetCents,
        @Pattern(regexp = "[A-Z]{3}") String currency,
        @Size(max = 2000) String notes
) {}
