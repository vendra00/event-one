package com.t1tanic.eventone.model.dto;

import com.t1tanic.eventone.model.enums.ProviderKind;
import jakarta.validation.constraints.Size;

public record UpsertMyProviderProfileReq(
        @Size(max = 200) String displayName,
        ProviderKind kind,              // required when creating
        String bio,
        @Size(max = 120) String city,
        @Size(max = 120) String region,
        @Size(min = 2, max = 2) String country, // ISO-3166-1 alpha-2
        Integer minGuests,
        Integer maxGuests,
        @Size(max = 400) String cuisines,
        @Size(max = 400) String services
) {}
