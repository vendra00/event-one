package com.t1tanic.eventone.repository.geo;


import com.t1tanic.eventone.model.geo.GeoCountry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GeoCountryRepository extends JpaRepository<GeoCountry, String> {

    // PK is the ISO-3166-1 alpha-2 code (e.g., "ES")
    Optional<GeoCountry> findByCodeIgnoreCase(String code);

    List<GeoCountry> findAllByOrderByNameAsc();

    boolean existsByCodeIgnoreCase(String code);
}
