package com.t1tanic.eventone.model.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

public record CreateEventRequestReq(
        @NotBlank @Size(max = 200) String title,
        Long offeringId,
        Long providerId,
        @NotNull LocalDateTime startsAt,
        @NotNull LocalDateTime endsAt,
        @NotNull @Min(1) Integer guests,
        @Valid GeoLocationInput geo,
        @JsonAlias({"cuisines"})
        @Size(max = 20, message = "At most 20 cuisines")
        List<@NotBlank @Pattern(regexp = "^[a-z0-9._-]{1,64}$", message = "Invalid cuisine code") String> cuisineCodes,
        @Size(max = 400) String services,
        @Min(0) Integer budgetCents,
        @Pattern(regexp = "[A-Z]{3}") String currency,
        @Size(max = 2000) String notes
) {}
