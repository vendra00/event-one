package com.t1tanic.eventone.controller;

import com.t1tanic.eventone.model.dto.AppUserDto;
import com.t1tanic.eventone.model.dto.request.CreateUserReq;
import com.t1tanic.eventone.model.dto.request.UpdateUserReq;
import com.t1tanic.eventone.service.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class AppUserController {

    private final AppUserService service;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> create(@RequestBody @Valid CreateUserReq req, UriComponentsBuilder ucb) {
        var dto = service.create(req);
        var location = ucb.path("/api/users/{id}").buildAndExpand(dto.id()).toUri();
        return ResponseEntity.created(location).build();
    }


    @GetMapping("/{id}")
    public AppUserDto get(@PathVariable Long id) {
        return service.get(id);
    }

    @GetMapping("/me")
    public AppUserDto me(@AuthenticationPrincipal Jwt jwt) {
        Long id = Long.valueOf(jwt.getSubject());
        return service.get(id);
    }

    @GetMapping("/by-email")
    public ResponseEntity<AppUserDto> byEmail(@RequestParam String email) {
        Optional<AppUserDto> dto = service.findByEmail(email);
        return dto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public Page<AppUserDto> list(@PageableDefault(size = 20) Pageable pageable) {
        return service.list(pageable);
    }

    @PutMapping("/{id}")
    public AppUserDto update(@PathVariable Long id, @RequestBody @Valid UpdateUserReq req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
