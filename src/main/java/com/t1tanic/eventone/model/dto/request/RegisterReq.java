package com.t1tanic.eventone.model.dto.request;

import com.t1tanic.eventone.model.enums.UserRole;

public record RegisterReq(
        @jakarta.validation.constraints.Email String email,
        @jakarta.validation.constraints.Size(min=8) String password,
        @jakarta.validation.constraints.NotNull UserRole role   // CONSUMER or PROVIDER
) {}
