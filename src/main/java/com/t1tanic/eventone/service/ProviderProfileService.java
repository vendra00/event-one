package com.t1tanic.eventone.service;

import com.t1tanic.eventone.model.dto.request.CreateProviderProfileReq;
import com.t1tanic.eventone.model.dto.ProviderProfileDto;
import com.t1tanic.eventone.model.dto.request.UpdateProviderProfileReq;
import com.t1tanic.eventone.model.dto.request.UpsertMyProviderProfileReq;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProviderProfileService {
    ProviderProfileDto create(CreateProviderProfileReq req);
    ProviderProfileDto get(Long id);
    Optional<ProviderProfileDto> findByUserId(Long userId);
    Page<ProviderProfileDto> list(Pageable pageable);
    ProviderProfileDto update(Long id, UpdateProviderProfileReq req);
    void delete(Long id);

    ProviderProfileDto getForUser(Long userId);
    ProviderProfileDto upsertForUser(Long userId, UpsertMyProviderProfileReq req);
    void deleteForUser(Long userId);
}
