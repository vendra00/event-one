package com.t1tanic.eventone.repository;

import com.t1tanic.eventone.model.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    // consumer
    Page<Booking> findByProposal_Request_Consumer_Id(Long consumerUserId, Pageable pageable);
    // provider
    Page<Booking> findByProposal_Provider_Id(Long providerId, Pageable pageable);
}