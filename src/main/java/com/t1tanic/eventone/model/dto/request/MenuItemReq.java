package com.t1tanic.eventone.model.dto.request;

import com.t1tanic.eventone.model.enums.Course;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MenuItemReq(
        @NotBlank @Size(max=200) String name,
        @Size(max=500) String description,
        @NotNull Course course
) {}
