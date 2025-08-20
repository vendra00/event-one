package com.t1tanic.eventone.repository.geo;

import com.t1tanic.eventone.model.geo.GeoPostalCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeoPostalCodeRepository extends JpaRepository<GeoPostalCode, String> {}
