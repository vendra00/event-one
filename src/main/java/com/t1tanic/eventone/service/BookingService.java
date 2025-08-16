package com.t1tanic.eventone.service;

import com.t1tanic.eventone.model.dto.BookingDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookingService {
    // consumer-side
    Page<BookingDto> listMine(Long consumerUserId, Pageable pageable);
    BookingDto getMine(Long consumerUserId, Long id);
    BookingDto cancelAsConsumer(Long consumerUserId, Long id);

    // provider-side
    Page<BookingDto> listForProvider(Long providerUserId, Pageable pageable);
    BookingDto getForProvider(Long providerUserId, Long id);
    BookingDto confirmAsProvider(Long providerUserId, Long id);
    BookingDto cancelAsProvider(Long providerUserId, Long id);
}
