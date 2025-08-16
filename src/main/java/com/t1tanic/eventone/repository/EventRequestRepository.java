package com.t1tanic.eventone.repository;

import com.t1tanic.eventone.model.EventRequest;
import com.t1tanic.eventone.model.enums.EventRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRequestRepository extends JpaRepository<EventRequest, Long> {
    Page<EventRequest> findByConsumerId(Long consumerId, Pageable pageable);
    Page<EventRequest> findByProviderId(Long providerId, Pageable pageable);

    // (optional) browse OPEN and untargeted by location
    Page<EventRequest> findByStatusAndProviderIsNullAndRegionIgnoreCase(EventRequestStatus status, String region, Pageable pageable);

    Page<EventRequest> findByStatusAndProviderIsNullAndCityIgnoreCaseAndRegionIgnoreCase(EventRequestStatus status, String city, String region, Pageable pageable);
}
