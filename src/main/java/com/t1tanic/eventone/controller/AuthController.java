package com.t1tanic.eventone.controller;

import com.t1tanic.eventone.model.dto.CreateUserReq;
import com.t1tanic.eventone.model.enums.UserRole;
import com.t1tanic.eventone.repository.AppUserRepository;
import com.t1tanic.eventone.service.AppUserService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AppUserService usersSvc;
    private final AppUserRepository usersRepo;
    private final PasswordEncoder encoder;
    private final JwtEncoder jwtEncoder;

    record RegisterReq(@Email String email, @Size(min=8) String password) {}
    record LoginReq(String email, String password) {}

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody RegisterReq req) {
        usersSvc.create(new CreateUserReq(req.email(), req.password(), Set.of(UserRole.CONSUMER)));
    }

    @PostMapping("/login")
    public Map<String,String> login(@RequestBody LoginReq req) {
        var user = usersRepo.findByEmail(req.email().trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("bad_credentials"));
        if (!encoder.matches(req.password(), user.getPasswordHash()))
            throw new IllegalArgumentException("bad_credentials");

        var now = Instant.now();
        var claims = JwtClaimsSet.builder()
                .subject(String.valueOf(user.getId()))
                .issuedAt(now)
                .expiresAt(now.plus(12, ChronoUnit.HOURS))
                .claim("email", user.getEmail())
                .build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        return Map.of("token", token);
    }
}
