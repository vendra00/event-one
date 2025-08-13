package com.t1tanic.eventone.service;

import com.t1tanic.eventone.model.dto.OfferingDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface SearchService {
    Page<OfferingDto> search(String city, String region, Integer guests, String cuisine, String service, LocalDateTime from, LocalDateTime to, Pageable pageable);
}
