package com.t1tanic.eventone.model.dto;

import com.t1tanic.eventone.model.enums.UserRole;
import java.time.Instant;
import java.util.Set;

public record AppUserDto(Long id, String email, Set<UserRole> roles, Instant createdAt) {}
