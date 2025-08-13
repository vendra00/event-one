package com.t1tanic.eventone.repository;

import com.t1tanic.eventone.model.EventRequest;
import com.t1tanic.eventone.model.enums.EventRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRequestRepository extends JpaRepository<EventRequest, Long> {
    Page<EventRequest> findByUserId(Long userId, Pageable pageable);
    Page<EventRequest> findByStatus(EventRequestStatus status, Pageable pageable);
}