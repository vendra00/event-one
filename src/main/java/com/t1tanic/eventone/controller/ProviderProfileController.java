package com.t1tanic.eventone.controller;

import com.t1tanic.eventone.model.dto.request.CreateProviderProfileReq;
import com.t1tanic.eventone.model.dto.ProviderProfileDto;
import com.t1tanic.eventone.model.dto.request.UpdateProviderProfileReq;
import com.t1tanic.eventone.service.ProviderProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("/api/provider")
@RequiredArgsConstructor
public class ProviderProfileController {

    private final ProviderProfileService service;

    @PreAuthorize("hasAnyRole('PROVIDER','ADMIN')")
    @PostMapping
    public ResponseEntity<ProviderProfileDto> create(@RequestBody @Valid CreateProviderProfileReq req) {
        var dto = service.create(req);
        return ResponseEntity.created(URI.create("/api/provider/" + dto.id())).body(dto);
    }

    @PreAuthorize("hasAnyRole('PROVIDER','ADMIN')")
    @GetMapping("/{id}")
    public ProviderProfileDto get(@PathVariable Long id) {
        return service.get(id);
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<ProviderProfileDto> byUser(@PathVariable Long userId) {
        return service.findByUserId(userId).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping
    public Page<ProviderProfileDto> list(@PageableDefault(size = 20) Pageable pageable) {
        return service.list(pageable);
    }

    @PreAuthorize("hasAnyRole('PROVIDER','ADMIN')")
    @PutMapping("/{id}")
    public ProviderProfileDto update(@PathVariable Long id, @RequestBody @Valid UpdateProviderProfileReq req) {
        return service.update(id, req);
    }

    @PreAuthorize("hasAnyRole('PROVIDER','ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
