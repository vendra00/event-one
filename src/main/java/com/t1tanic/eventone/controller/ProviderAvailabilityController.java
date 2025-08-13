package com.t1tanic.eventone.controller;

import com.t1tanic.eventone.model.dto.AvailabilitySlotDto;
import com.t1tanic.eventone.model.dto.request.CreateAvailabilityReq;
import com.t1tanic.eventone.model.dto.request.UpdateAvailabilityReq;
import com.t1tanic.eventone.service.AvailabilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/providers/me/availability")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('PROVIDER','ADMIN')")
public class ProviderAvailabilityController {

    private final AvailabilityService service;
    private static Long uid(Jwt jwt) { return Long.valueOf(jwt.getSubject()); }

    // List my slots (optionally filter by overlap with [from,to])
    @GetMapping
    public List<AvailabilitySlotDto> list(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return service.listMine(uid(jwt), from, to);
    }

    // Create (preventOverlap defaults true for safety)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AvailabilitySlotDto create(@AuthenticationPrincipal Jwt jwt,
                                      @RequestBody @Valid CreateAvailabilityReq req,
                                      @RequestParam(defaultValue = "true") boolean preventOverlap) {
        return service.create(uid(jwt), req, preventOverlap);
    }

    @PutMapping("/{id}")
    public AvailabilitySlotDto update(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id,
                                      @RequestBody @Valid UpdateAvailabilityReq req,
                                      @RequestParam(defaultValue = "true") boolean preventOverlap) {
        return service.update(uid(jwt), id, req, preventOverlap);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        service.delete(uid(jwt), id);
    }
}
