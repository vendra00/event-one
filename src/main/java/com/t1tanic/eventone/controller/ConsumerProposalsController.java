package com.t1tanic.eventone.controller;

import com.t1tanic.eventone.model.dto.BookingDto;
import com.t1tanic.eventone.model.dto.ProposalDto;
import com.t1tanic.eventone.service.ProposalService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/requests/{requestId}/proposals")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('CONSUMER','ADMIN')")
public class ConsumerProposalsController {
    private final ProposalService service;
    private static Long uid(Jwt jwt) { return Long.valueOf(jwt.getSubject()); }

    @GetMapping
    public Page<ProposalDto> listForMyRequest(@AuthenticationPrincipal Jwt jwt,
                                              @PathVariable Long requestId,
                                              @PageableDefault(size=20, sort="id", direction=Sort.Direction.DESC) Pageable pageable) {
        return service.listForRequestAsConsumer(uid(jwt), requestId, pageable);
    }

    @PostMapping("/{proposalId}/accept")
    public BookingDto accept(@AuthenticationPrincipal Jwt jwt,
                             @PathVariable Long requestId,
                             @PathVariable Long proposalId) {
        return service.accept(uid(jwt), requestId, proposalId);
    }

    @PostMapping("/{proposalId}/reject")
    public void reject(@AuthenticationPrincipal Jwt jwt,
                       @PathVariable Long requestId,
                       @PathVariable Long proposalId) {
        service.reject(uid(jwt), requestId, proposalId);
    }
}
