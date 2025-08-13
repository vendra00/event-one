package com.t1tanic.eventone.model.dto;

import com.t1tanic.eventone.model.enums.Course;

public record MenuItemDto(
        Long id,
        String name,
        String description,
        Course course) {}
