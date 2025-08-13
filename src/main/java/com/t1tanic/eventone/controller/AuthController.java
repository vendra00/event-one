package com.t1tanic.eventone.controller;

import com.t1tanic.eventone.model.dto.CreateUserReq;
import com.t1tanic.eventone.model.dto.LoginReq;
import com.t1tanic.eventone.model.dto.RegisterReq;
import com.t1tanic.eventone.repository.AppUserRepository;
import com.t1tanic.eventone.service.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AppUserService usersSvc;
    private final AppUserRepository usersRepo;
    private final PasswordEncoder encoder;
    private final JwtEncoder jwtEncoder;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody RegisterReq req) {
        var roles = java.util.Set.of(req.role());
        usersSvc.create(new CreateUserReq(req.email(), req.password(), roles));
    }

    @PostMapping("/login")
    public Map<String,String> login(@RequestBody LoginReq req) {
        var user = usersRepo.findByEmail(req.email().trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("bad_credentials"));
        if (!encoder.matches(req.password(), user.getPasswordHash()))
            throw new IllegalArgumentException("bad_credentials");

        var now = Instant.now();
        var roles = user.getRoles().stream().map(Enum::name).toList(); // ["CONSUMER", "PROVIDER", ...]

        var claims = JwtClaimsSet.builder()
                .subject(String.valueOf(user.getId()))
                .issuedAt(now)
                .expiresAt(now.plus(12, ChronoUnit.HOURS))
                .claim("email", user.getEmail())
                .claim("roles", roles)     // <= add roles
                .build();

        String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        return Map.of("token", token);
    }
}
