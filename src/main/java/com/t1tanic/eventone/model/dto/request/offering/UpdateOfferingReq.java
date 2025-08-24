package com.t1tanic.eventone.model.dto.request.offering;// UpdateOfferingReq.java


import com.t1tanic.eventone.model.dto.request.MenuItemPatch;

import java.util.List;

public record UpdateOfferingReq(
        String title,
        String description,
        Integer basePriceCents,
        String currency,
        Integer minGuests,
        Integer maxGuests,
        String cuisines,
        String services,
        String city,
        String region,
        Boolean active,
        // full replace of menu for simplicity (send whole array); omit/null to keep as is
        List<MenuItemPatch> menu
) {}
