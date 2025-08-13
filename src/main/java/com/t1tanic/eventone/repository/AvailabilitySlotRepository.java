package com.t1tanic.eventone.repository;

import com.t1tanic.eventone.model.AvailabilitySlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AvailabilitySlotRepository extends JpaRepository<AvailabilitySlot, Long> {
    List<AvailabilitySlot> findByProviderIdAndStartsAtLessThanEqualAndEndsAtGreaterThanEqual(
            Long providerId, LocalDateTime startsAt, LocalDateTime endsAt);
}