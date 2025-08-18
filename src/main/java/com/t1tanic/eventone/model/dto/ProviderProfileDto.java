package com.t1tanic.eventone.model.dto;

public record ProviderProfileDto(
        Long id,
        Long userId,
        String displayName,
        com.t1tanic.eventone.model.enums.ProviderKind kind,
        String bio,
        GeoLocationDto location,
        Integer minGuests,
        Integer maxGuests,
        String cuisines,
        String services
) {}
