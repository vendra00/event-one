package com.t1tanic.eventone.model.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record GeoLocationReq(
        @Size(min = 2, max = 2) String countryCode,         // e.g., "ES"
        @Size(max = 10) String communityCode,               // e.g., "ES-MD"
        @Size(max = 10) String provinceCode,                // e.g., "ES-M"
        @Size(max = 10) String municipalityCode,            // INE code (string)
        @Size(max = 120) String locality,                   // barrio/localidad/isla (optional)
        @Pattern(regexp = "\\d{5}") String postalCode       // Spanish ZIP (00000)
) {}
