package com.t1tanic.eventone.model.dto;

import com.t1tanic.eventone.model.enums.ProviderKind;

public record UpdateProviderProfileReq(
        String displayName,
        ProviderKind kind,
        String bio,
        String city,
        String region,
        String country,
        Integer minGuests,
        Integer maxGuests,
        String cuisines,
        String services
) {}
