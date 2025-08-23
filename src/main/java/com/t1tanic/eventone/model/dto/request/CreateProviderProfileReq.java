package com.t1tanic.eventone.model.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.t1tanic.eventone.model.enums.ProviderKind;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateProviderProfileReq(
        @NotNull Long userId,
        @NotBlank @Size(max = 200) String displayName,
        @NotNull ProviderKind kind,
        String bio,
        @NotNull GeoLocationInput geo,        // <— NEW
        Integer minGuests,
        Integer maxGuests,
        @JsonAlias({"cuisines"})
        @Size(max = 30) List<
                        @NotBlank
                        @Pattern(regexp="^[a-z0-9._-]{1,64}$") String
                        > cuisineCodes,
        @Size(max = 400) String services
) {}
