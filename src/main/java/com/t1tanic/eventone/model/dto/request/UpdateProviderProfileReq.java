package com.t1tanic.eventone.model.dto.request;

import com.t1tanic.eventone.model.enums.ProviderKind;
import jakarta.validation.constraints.Size;

public record UpdateProviderProfileReq(
        @Size(max = 200) String displayName,
        ProviderKind kind,
        String bio,
        GeoLocationInput geo,                 // <— NEW
        Integer minGuests,
        Integer maxGuests,
        @Size(max = 400) String cuisines,
        @Size(max = 400) String services
) {}
