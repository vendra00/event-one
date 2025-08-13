package com.t1tanic.eventone.model.dto;

import com.t1tanic.eventone.model.enums.UserRole;
import java.util.Set;

public record CreateUserReq(String email, String password, Set<UserRole> roles) {}
