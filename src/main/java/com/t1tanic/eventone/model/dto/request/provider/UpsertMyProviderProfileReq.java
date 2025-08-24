package com.t1tanic.eventone.model.dto.request.provider;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.t1tanic.eventone.model.dto.request.GeoLocationInput;
import com.t1tanic.eventone.model.enums.ProviderKind;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UpsertMyProviderProfileReq(
        @Size(max = 200) String displayName,
        ProviderKind kind,                  // required when creating
        String bio,
        GeoLocationInput geo,                 // <— NEW
        Integer minGuests,
        Integer maxGuests,
        @JsonAlias({"cuisines"})
        List<String> cuisineCodes,
        @Size(max = 400) String services
) {}
