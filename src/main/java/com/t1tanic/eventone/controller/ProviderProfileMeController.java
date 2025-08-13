package com.t1tanic.eventone.controller;

import com.t1tanic.eventone.model.dto.ProviderProfileDto;
import com.t1tanic.eventone.model.dto.UpsertMyProviderProfileReq;
import com.t1tanic.eventone.service.ProviderProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/providers/me")
@RequiredArgsConstructor
public class ProviderProfileMeController {

    private final ProviderProfileService service;

    private static Long uid(Jwt jwt) { return Long.valueOf(jwt.getSubject()); }

    @GetMapping
    public ProviderProfileDto me(@AuthenticationPrincipal Jwt jwt) {
        return service.getForUser(uid(jwt));
    }

    @PutMapping
    public ProviderProfileDto upsert(@AuthenticationPrincipal Jwt jwt, @RequestBody @Valid UpsertMyProviderProfileReq req) {
        return service.upsertForUser(uid(jwt), req);
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@AuthenticationPrincipal Jwt jwt) {
        service.deleteForUser(uid(jwt));
        return ResponseEntity.noContent().build();
    }
}
