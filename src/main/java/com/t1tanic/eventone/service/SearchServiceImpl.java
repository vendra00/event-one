package com.t1tanic.eventone.service;

import com.t1tanic.eventone.model.Offering;
import com.t1tanic.eventone.model.dto.OfferingDto;
import com.t1tanic.eventone.model.mapper.OfferingMapper;
import com.t1tanic.eventone.repository.OfferingRepository;
import com.t1tanic.eventone.repository.spec.OfferingSpecs;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchServiceImpl implements SearchService {

    private final OfferingRepository offerings;
    private final OfferingMapper mapper;

    @Override
    public Page<OfferingDto> search(String city, String region, Integer guests,
                                    String cuisine, String service,
                                    LocalDateTime from, LocalDateTime to,
                                    Pageable pageable) {

        Specification<Offering> spec = Specification.allOf(
                OfferingSpecs.activeOnly(),
                OfferingSpecs.city(city),
                OfferingSpecs.region(region),
                OfferingSpecs.guests(guests),
                OfferingSpecs.hasCuisine(cuisine),
                OfferingSpecs.hasService(service),
                OfferingSpecs.availableBetween(from, to)
        );

        return offerings.findAll(spec, pageable).map(mapper::toDto);
    }
}
