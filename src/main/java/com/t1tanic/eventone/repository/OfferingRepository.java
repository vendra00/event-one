package com.t1tanic.eventone.repository;

import com.t1tanic.eventone.model.Offering;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfferingRepository extends JpaRepository<Offering, Long> {
    Page<Offering> findByProviderId(Long providerId, Pageable pageable);
    Page<Offering> findByCityIgnoreCaseAndRegionIgnoreCase(String city, String region, Pageable pageable);
}
