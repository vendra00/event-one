// CreateOfferingReq.java
package com.t1tanic.eventone.model.dto.request.offering;

import com.t1tanic.eventone.model.dto.request.MenuItemReq;
import jakarta.validation.constraints.*;
import java.util.List;

public record CreateOfferingReq(
        @NotBlank @Size(max=200) String title,
        String description,
        @NotNull @Min(0) Integer basePriceCents,
        @NotBlank @Size(min=3, max=3) String currency,
        @Min(1) Integer minGuests,
        @Min(1) Integer maxGuests,
        @Size(max=400) String cuisines,
        @Size(max=400) String services,
        @Size(max=120) String city,
        @Size(max=120) String region,
        List<MenuItemReq> menu
) {}
