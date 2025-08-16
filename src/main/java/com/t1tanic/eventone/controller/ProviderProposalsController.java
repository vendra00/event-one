package com.t1tanic.eventone.controller;

import com.t1tanic.eventone.model.dto.ProposalDto;
import com.t1tanic.eventone.model.dto.request.CreateProposalReq;
import com.t1tanic.eventone.service.ProposalService;
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
@RequestMapping("/api/providers/me/requests/{requestId}/proposals")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('PROVIDER','ADMIN')")
public class ProviderProposalsController {
    private final ProposalService service;
    private static Long uid(Jwt jwt) { return Long.valueOf(jwt.getSubject()); }

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public ProposalDto create(@AuthenticationPrincipal Jwt jwt,
                              @PathVariable Long requestId,
                              @RequestBody @Valid CreateProposalReq req) {
        return service.create(uid(jwt), requestId, req);
    }

    // Optional: provider list of all own proposals
    @GetMapping("/../../proposals") // NOT a real path; prefer separate controller if needed
    public Page<ProposalDto> listMine(@AuthenticationPrincipal Jwt jwt,
                                      @PageableDefault(size=20, sort="id", direction=Sort.Direction.DESC) Pageable pageable) {
        return service.listForProvider(uid(jwt), pageable);
    }
}
