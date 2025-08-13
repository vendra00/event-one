package com.t1tanic.eventone.model.mapper;

import com.t1tanic.eventone.model.AppUser;
import com.t1tanic.eventone.model.dto.AppUserDto;
import com.t1tanic.eventone.model.enums.UserRole;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Set;

@Component
public class AppUserMapper {

    public AppUserDto toDto(AppUser u) {
        if (u == null) return null;
        // Force initialization inside the service transaction and detach:
        Set<UserRole> roles = (u.getRoles() == null) ? Set.of() : Set.copyOf(u.getRoles());
        return new AppUserDto(u.getId(), u.getEmail(), roles, u.getCreatedAt());
    }

    public String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }
}
