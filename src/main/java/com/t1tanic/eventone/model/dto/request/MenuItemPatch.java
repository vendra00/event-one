package com.t1tanic.eventone.model.dto.request;

import com.t1tanic.eventone.model.enums.Course;

public record MenuItemPatch(Long id, String name, String description, Course course) {}
