package com.t1tanic.eventone.model.dto;

import java.util.List;

public record ProviderProfileDto(
        Long id,
        Long userId,
        String displayName,
        com.t1tanic.eventone.model.enums.ProviderKind kind,
        String bio,
        GeoLocationDto location,
        Integer minGuests,
        Integer maxGuests,
        List<CuisineDto> cuisines,
        String services
) {}
