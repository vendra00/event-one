package com.t1tanic.eventone.service;

import com.t1tanic.eventone.model.dto.AppUserDto;
import com.t1tanic.eventone.model.dto.request.CreateUserReq;
import com.t1tanic.eventone.model.dto.request.UpdateUserReq;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AppUserService {
    AppUserDto create(CreateUserReq req);
    AppUserDto get(Long id);
    Optional<AppUserDto> findByEmail(String email);
    Page<AppUserDto> list(Pageable pageable);
    AppUserDto update(Long id, UpdateUserReq req);
    void delete(Long id);
}
