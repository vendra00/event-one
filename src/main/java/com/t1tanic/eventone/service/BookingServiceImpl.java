package com.t1tanic.eventone.service;

import com.t1tanic.eventone.model.enums.BookingStatus;
import com.t1tanic.eventone.model.mapper.BookingMapper;
import com.t1tanic.eventone.repository.BookingRepository;
import com.t1tanic.eventone.repository.ProviderProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookings;
    private final ProviderProfileRepository providers;
    private final BookingMapper mapper;

    // consumer
    @Override @Transactional(readOnly = true)
    public Page<com.t1tanic.eventone.model.dto.BookingDto> listMine(Long consumerUserId, Pageable pageable) {
        return bookings.findByProposal_Request_Consumer_Id(consumerUserId, pageable).map(mapper::toDto);
    }

    @Override @Transactional(readOnly = true)
    public com.t1tanic.eventone.model.dto.BookingDto getMine(Long consumerUserId, Long id) {
        var b = bookings.findById(id)
                .filter(x -> x.getProposal().getRequest().getConsumer().getId().equals(consumerUserId))
                .orElseThrow(() -> new EntityNotFoundException("booking_not_found_or_not_owned:" + id));
        return mapper.toDto(b);
    }

    @Override
    public com.t1tanic.eventone.model.dto.BookingDto cancelAsConsumer(Long consumerUserId, Long id) {
        var b = bookings.findById(id)
                .filter(x -> x.getProposal().getRequest().getConsumer().getId().equals(consumerUserId))
                .orElseThrow(() -> new EntityNotFoundException("booking_not_found_or_not_owned:" + id));
        if (b.getStatus() == BookingStatus.CANCELLED) return mapper.toDto(b);
        b.setStatus(BookingStatus.CANCELLED);
        return mapper.toDto(bookings.save(b));
    }

    // provider
    @Override @Transactional(readOnly = true)
    public Page<com.t1tanic.eventone.model.dto.BookingDto> listForProvider(Long providerUserId, Pageable pageable) {
        var provider = providers.findByUserId(providerUserId)
                .orElseThrow(() -> new EntityNotFoundException("provider_profile_not_found_for_user:" + providerUserId));
        return bookings.findByProposal_Provider_Id(provider.getId(), pageable).map(mapper::toDto);
    }

    @Override @Transactional(readOnly = true)
    public com.t1tanic.eventone.model.dto.BookingDto getForProvider(Long providerUserId, Long id) {
        var provider = providers.findByUserId(providerUserId)
                .orElseThrow(() -> new EntityNotFoundException("provider_profile_not_found_for_user:" + providerUserId));
        var b = bookings.findById(id)
                .filter(x -> x.getProposal().getProvider().getId().equals(provider.getId()))
                .orElseThrow(() -> new EntityNotFoundException("booking_not_found_or_not_owned:" + id));
        return mapper.toDto(b);
    }

    @Override
    public com.t1tanic.eventone.model.dto.BookingDto confirmAsProvider(Long providerUserId, Long id) {
        var provider = providers.findByUserId(providerUserId)
                .orElseThrow(() -> new EntityNotFoundException("provider_profile_not_found_for_user:" + providerUserId));
        var b = bookings.findById(id)
                .filter(x -> x.getProposal().getProvider().getId().equals(provider.getId()))
                .orElseThrow(() -> new EntityNotFoundException("booking_not_found_or_not_owned:" + id));
        if (b.getStatus() == BookingStatus.CANCELLED) throw new IllegalStateException("booking_cancelled");
        b.setStatus(BookingStatus.CONFIRMED);
        return mapper.toDto(bookings.save(b));
    }

    @Override
    public com.t1tanic.eventone.model.dto.BookingDto cancelAsProvider(Long providerUserId, Long id) {
        var provider = providers.findByUserId(providerUserId)
                .orElseThrow(() -> new EntityNotFoundException("provider_profile_not_found_for_user:" + providerUserId));
        var b = bookings.findById(id)
                .filter(x -> x.getProposal().getProvider().getId().equals(provider.getId()))
                .orElseThrow(() -> new EntityNotFoundException("booking_not_found_or_not_owned:" + id));
        b.setStatus(BookingStatus.CANCELLED);
        return mapper.toDto(bookings.save(b));
    }
}
