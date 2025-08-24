package com.t1tanic.eventone.model.dto.request.proposal;

import jakarta.validation.constraints.*;

public record CreateProposalReq(
        @NotNull @Min(0) Integer priceCents,
        @Size(min=3,max=3) String currency,
        @Size(max=1000) String message,
        Long offeringId // optional
) {}
