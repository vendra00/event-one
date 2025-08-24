package com.t1tanic.eventone.repository.geo;

import com.t1tanic.eventone.model.geo.GeoCommunity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GeoCommunityRepository extends JpaRepository<GeoCommunity, String> {
    List<GeoCommunity> findAllByCountry_CodeOrderByNameAsc(String countryCode);
    Optional<GeoCommunity> findByNameIgnoreCaseAndCountry_Code(String name, String countryCode);
}
