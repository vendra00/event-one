package com.t1tanic.eventone.model.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record GeoLocationInput(
        @Size(max = 10) String communityCode,      // e.g., ES-MD
        @Size(max = 10) String provinceCode,       // e.g., ES-M
        @Size(max = 10) String municipalityCode,   // e.g., 28079

        @Size(max = 120) String communityName,     // e.g., Comunidad de Madrid
        @Size(max = 120) String provinceName,      // e.g., Madrid
        @Size(max = 120) String municipalityName,  // e.g., Madrid

        @Size(max = 120) String locality,          // optional (barrio/zona)
        @Pattern(regexp = "\\d{5}") String postalCode // optional 5-digit
) {}
