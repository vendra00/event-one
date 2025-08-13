package com.t1tanic.eventone.repository;

import com.t1tanic.eventone.model.Booking;
import com.t1tanic.eventone.model.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findByStatus(BookingStatus status, Pageable pageable);
    // convenience lookups
    boolean existsByProposalId(Long proposalId);
}