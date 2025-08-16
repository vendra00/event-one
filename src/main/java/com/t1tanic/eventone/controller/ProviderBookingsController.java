package com.t1tanic.eventone.controller;

import com.t1tanic.eventone.model.dto.BookingDto;
import com.t1tanic.eventone.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/providers/me/bookings")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('PROVIDER','ADMIN')")
public class ProviderBookingsController {

    private final BookingService service;
    private static Long uid(Jwt jwt) { return Long.valueOf(jwt.getSubject()); }

    @GetMapping
    public Page<BookingDto> list(@AuthenticationPrincipal Jwt jwt,
                                 @PageableDefault(size=20, sort="id", direction=Sort.Direction.DESC) Pageable pageable) {
        return service.listForProvider(uid(jwt), pageable);
    }

    @GetMapping("/{id}")
    public BookingDto get(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        return service.getForProvider(uid(jwt), id);
    }

    @PostMapping("/{id}/confirm")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto confirm(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        return service.confirmAsProvider(uid(jwt), id);
    }

    @PostMapping("/{id}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto cancel(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        return service.cancelAsProvider(uid(jwt), id);
    }
}
