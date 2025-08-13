// src/main/java/com/t1tanic/eventone/service/AppUserServiceImpl.java
package com.t1tanic.eventone.service;

import com.t1tanic.eventone.model.AppUser;
import com.t1tanic.eventone.model.dto.AppUserDto;
import com.t1tanic.eventone.model.dto.CreateUserReq;
import com.t1tanic.eventone.model.dto.UpdateUserReq;
import com.t1tanic.eventone.model.enums.UserRole;
import com.t1tanic.eventone.model.mapper.AppUserMapper;
import com.t1tanic.eventone.repository.AppUserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final AppUserMapper mapper;

    @Override
    public AppUserDto create(CreateUserReq req) {
        final String email = mapper.normalizeEmail(req.email());
        users.findByEmail(email).ifPresent(u -> { throw new IllegalArgumentException("email_in_use"); });

        var u = new AppUser();
        u.setEmail(email);
        u.setPasswordHash(passwordEncoder.encode(req.password()));
        if (req.roles() == null || req.roles().isEmpty()) {
            u.getRoles().add(UserRole.CONSUMER);
        } else {
            u.getRoles().addAll(req.roles());
        }
        var saved = users.save(u);
        log.info("Created user id={} email={}", saved.getId(), saved.getEmail());
        return mapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AppUserDto get(Long id) {
        return users.findById(id).map(mapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("user_not_found:" + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AppUserDto> findByEmail(String email) {
        return users.findByEmail(mapper.normalizeEmail(email)).map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AppUserDto> list(Pageable pageable) {
        return users.findAll(pageable).map(mapper::toDto);
    }

    @Override
    public AppUserDto update(Long id, UpdateUserReq req) {
        var u = users.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("user_not_found:" + id));

        if (req.email() != null && !req.email().isBlank()) {
            var newEmail = mapper.normalizeEmail(req.email());
            users.findByEmail(newEmail)
                    .filter(other -> !other.getId().equals(id))
                    .ifPresent(other -> { throw new IllegalArgumentException("email_in_use"); });
            u.setEmail(newEmail);
        }
        if (req.password() != null && !req.password().isBlank()) {
            u.setPasswordHash(passwordEncoder.encode(req.password()));
        }
        if (req.roles() != null && !req.roles().isEmpty()) {
            u.getRoles().clear();
            u.getRoles().addAll(req.roles());
        }

        var saved = users.save(u);
        log.info("Updated user id={}", saved.getId());
        return mapper.toDto(saved);
    }

    @Override
    public void delete(Long id) {
        if (!users.existsById(id)) throw new EntityNotFoundException("user_not_found:" + id);
        users.deleteById(id);
        log.info("Deleted user id={}", id);
    }
}
