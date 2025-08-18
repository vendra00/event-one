package com.t1tanic.eventone.model.dto.request;

import com.t1tanic.eventone.model.enums.ProviderKind;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateProviderProfileReq(
        @NotNull Long userId,
        @NotBlank @Size(max = 200) String displayName,
        @NotNull ProviderKind kind,
        String bio,
        @NotNull GeoLocationInput geo,        // <— NEW
        Integer minGuests,
        Integer maxGuests,
        @Size(max = 400) String cuisines,
        @Size(max = 400) String services
) {}
