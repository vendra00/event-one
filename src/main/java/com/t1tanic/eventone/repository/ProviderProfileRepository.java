package com.t1tanic.eventone.repository;

import com.t1tanic.eventone.model.ProviderProfile;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProviderProfileRepository extends JpaRepository<ProviderProfile, Long> {
    @EntityGraph(attributePaths = {
            "location.country",
            "location.community",
            "location.province",
            "location.municipality"
    })
    Optional<ProviderProfile> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
}