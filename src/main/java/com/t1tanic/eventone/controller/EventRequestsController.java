package com.t1tanic.eventone.controller;

import com.t1tanic.eventone.model.dto.EventRequestDto;
import com.t1tanic.eventone.model.dto.request.CreateEventRequestReq;
import com.t1tanic.eventone.service.EventRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('CONSUMER','ADMIN')")
public class EventRequestsController {
    private final EventRequestService service;
    private static Long uid(Jwt jwt) { return Long.valueOf(jwt.getSubject()); }

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public EventRequestDto create(@AuthenticationPrincipal Jwt jwt, @RequestBody @Valid CreateEventRequestReq req) {
        return service.create(uid(jwt), req);
    }

    @GetMapping
    public Page<EventRequestDto> list(@AuthenticationPrincipal Jwt jwt, @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return service.listMine(uid(jwt), pageable);
    }

    @GetMapping("/{id}")
    public EventRequestDto get(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        return service.getMine(uid(jwt), id);
    }

    @PostMapping("/{id}/cancel")
    public EventRequestDto cancel(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        return service.cancelMine(uid(jwt), id);
    }
}
