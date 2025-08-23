package com.t1tanic.eventone.model.dto;


import java.util.List;

public record OfferingDto(
        Long id,
        Long providerId,
        String title,
        String description,
        Integer basePriceCents,
        String currency,
        Integer minGuests,
        Integer maxGuests,
        List<CuisineDto> cuisines,
        String services,
        String city,
        String region,
        boolean active,
        List<MenuItemDto> menu
) {}

