package com.t1tanic.eventone.controller;

import com.t1tanic.eventone.model.dto.CreateProviderProfileReq;
import com.t1tanic.eventone.model.dto.ProviderProfileDto;
import com.t1tanic.eventone.model.dto.UpdateProviderProfileReq;
import com.t1tanic.eventone.service.ProviderProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/provider")
@RequiredArgsConstructor
public class ProviderProfileController {

    private final ProviderProfileService service;

    @PostMapping
    public ResponseEntity<ProviderProfileDto> create(@RequestBody @Valid CreateProviderProfileReq req) {
        var dto = service.create(req);
        return ResponseEntity.created(URI.create("/api/provider/" + dto.id())).body(dto);
    }

    @GetMapping("/{id}")
    public ProviderProfileDto get(@PathVariable Long id) {
        return service.get(id);
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<ProviderProfileDto> byUser(@PathVariable Long userId) {
        return service.findByUserId(userId).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public Page<ProviderProfileDto> list(@PageableDefault(size = 20) Pageable pageable) {
        return service.list(pageable);
    }

    @PutMapping("/{id}")
    public ProviderProfileDto update(@PathVariable Long id, @RequestBody @Valid UpdateProviderProfileReq req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
