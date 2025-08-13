
package com.t1tanic.eventone.service;

import com.t1tanic.eventone.model.dto.request.CreateProviderProfileReq;
import com.t1tanic.eventone.model.dto.ProviderProfileDto;
import com.t1tanic.eventone.model.dto.request.UpdateProviderProfileReq;
import com.t1tanic.eventone.model.dto.request.UpsertMyProviderProfileReq;
import com.t1tanic.eventone.model.mapper.ProviderProfileMapper;
import com.t1tanic.eventone.repository.AppUserRepository;
import com.t1tanic.eventone.repository.ProviderProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProviderProfileServiceImpl implements ProviderProfileService {

    private final ProviderProfileRepository profiles;
    private final AppUserRepository users;
    private final ProviderProfileMapper mapper;

    @Override
    public ProviderProfileDto create(CreateProviderProfileReq req) {
        // one profile per user
        if (profiles.existsByUserId(req.userId())) {
            throw new IllegalArgumentException("provider_profile_exists_for_user");
        }

        var user = users.findById(req.userId())
                .orElseThrow(() -> new EntityNotFoundException("user_not_found:" + req.userId()));

        var entity = mapper.from(req);
        entity.setUser(user);
        var saved = profiles.save(entity);
        log.info("Created provider profile id={} userId={}", saved.getId(), saved.getUser().getId());
        return mapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ProviderProfileDto get(Long id) {
        return profiles.findById(id).map(mapper::toDto).orElseThrow(() -> new EntityNotFoundException("provider_profile_not_found:" + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProviderProfileDto> findByUserId(Long userId) {
        return profiles.findByUserId(userId).map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProviderProfileDto> list(Pageable pageable) {
        return profiles.findAll(pageable).map(mapper::toDto);
    }

    @Override
    public ProviderProfileDto update(Long id, UpdateProviderProfileReq req) {
        var entity = profiles.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("provider_profile_not_found:" + id));

        mapper.apply(req, entity);
        var saved = profiles.save(entity);
        log.info("Updated provider profile id={}", saved.getId());
        return mapper.toDto(saved);
    }

    @Override
    public void delete(Long id) {
        if (!profiles.existsById(id)) throw new EntityNotFoundException("provider_profile_not_found:" + id);
        profiles.deleteById(id);
        log.info("Deleted provider profile id={}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public ProviderProfileDto getForUser(Long userId) {
        return profiles.findByUserId(userId)
                .map(mapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("provider_profile_not_found_for_user:" + userId));
    }

    @Override
    public ProviderProfileDto upsertForUser(Long userId, UpsertMyProviderProfileReq req) {
        var user = users.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("user_not_found:" + userId));

        var existing = profiles.findByUserId(userId);
        if (existing.isPresent()) {
            var p = existing.get();
            mapper.apply(req, p);
            var saved = profiles.save(p);
            log.info("Updated provider profile id={} (me)", saved.getId());
            return mapper.toDto(saved);
        } else {
            // Creating: require displayName + kind
            if (req.displayName() == null || req.displayName().isBlank() || req.kind() == null) {
                throw new IllegalArgumentException("displayName_and_kind_required_for_create");
            }
            var p = mapper.from(req);
            p.setUser(user);
            var saved = profiles.save(p);
            log.info("Created provider profile id={} for userId={}", saved.getId(), userId);
            return mapper.toDto(saved);
        }
    }

    @Override
    public void deleteForUser(Long userId) {
        var p = profiles.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("provider_profile_not_found_for_user:" + userId));
        profiles.delete(p);
        log.info("Deleted provider profile id={} (me)", p.getId());
    }
}
