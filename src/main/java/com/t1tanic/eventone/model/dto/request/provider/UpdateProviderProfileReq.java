package com.t1tanic.eventone.model.dto.request.provider;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.t1tanic.eventone.model.dto.request.GeoLocationInput;
import jakarta.validation.Valid;
import java.util.List;

public record UpdateProviderProfileReq(
        String displayName,
        com.t1tanic.eventone.model.enums.ProviderKind kind,
        String bio,
        @Valid GeoLocationInput geo,
        Integer minGuests,
        Integer maxGuests,

        @JsonAlias({"cuisines"})
        List<String> cuisineCodes,   // optional; when present, fully replaces set

        String services,
        Boolean active               // if you have it
) {}

