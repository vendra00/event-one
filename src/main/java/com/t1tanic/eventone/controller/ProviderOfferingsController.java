package com.t1tanic.eventone.controller;

import com.t1tanic.eventone.model.dto.OfferingDto;
import com.t1tanic.eventone.model.dto.request.offering.CreateOfferingReq;
import com.t1tanic.eventone.model.dto.request.offering.UpdateOfferingReq;
import com.t1tanic.eventone.service.OfferingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/providers/me/offerings")
@RequiredArgsConstructor
public class ProviderOfferingsController {

    private final OfferingsService service;

    private static Long uid(Jwt jwt) { return Long.valueOf(jwt.getSubject()); }

    @PreAuthorize("hasAnyRole('PROVIDER','ADMIN')")
    @GetMapping
    public Page<OfferingDto> list(@AuthenticationPrincipal Jwt jwt,
                                  @PageableDefault(size = 20) Pageable pageable) {
        return service.listMine(uid(jwt), pageable);
    }

    @PreAuthorize("hasAnyRole('PROVIDER','ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OfferingDto create(@AuthenticationPrincipal Jwt jwt,
                              @RequestBody @Valid CreateOfferingReq req) {
        return service.create(uid(jwt), req);
    }

    @PreAuthorize("hasAnyRole('PROVIDER','ADMIN')")
    @GetMapping("/{id}")
    public OfferingDto get(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        return service.getMine(uid(jwt), id);
    }

    @PreAuthorize("hasAnyRole('PROVIDER','ADMIN')")
    @PutMapping("/{id}")
    public OfferingDto update(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id, @RequestBody @Valid UpdateOfferingReq req) {
        return service.update(uid(jwt), id, req);
    }

    @PreAuthorize("hasAnyRole('PROVIDER','ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        service.delete(uid(jwt), id);
    }
}
