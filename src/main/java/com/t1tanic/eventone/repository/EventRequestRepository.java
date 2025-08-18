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

    // Geo-aware (normalized)
    Page<EventRequest> findByStatusAndProviderIsNullAndLocation_Municipality_CodeIgnoreCase(
            EventRequestStatus status, String municipalityCode, Pageable pageable);

    Page<EventRequest> findByStatusAndProviderIsNullAndLocation_Province_CodeIgnoreCase(
            EventRequestStatus status, String provinceCode, Pageable pageable);

    Page<EventRequest> findByStatusAndProviderIsNullAndLocation_Community_CodeIgnoreCase(
            EventRequestStatus status, String communityCode, Pageable pageable);
}
