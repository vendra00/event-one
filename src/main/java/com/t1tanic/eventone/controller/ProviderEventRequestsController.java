package com.t1tanic.eventone.controller;

import com.t1tanic.eventone.model.dto.EventRequestDto;
import com.t1tanic.eventone.service.EventRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/providers/me/requests")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('PROVIDER','ADMIN')")
public class ProviderEventRequestsController {
    private final EventRequestService service;
    private static Long uid(Jwt jwt) { return Long.valueOf(jwt.getSubject()); }

    @GetMapping
    public Page<EventRequestDto> listMine(@AuthenticationPrincipal Jwt jwt, @RequestParam(defaultValue = "false") boolean includeUntargetedOpenNearby, @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return service.listForProvider(uid(jwt), includeUntargetedOpenNearby, pageable);
    }

    @GetMapping("/{id}")
    public EventRequestDto get(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        return service.getForProvider(uid(jwt), id);
    }
}
