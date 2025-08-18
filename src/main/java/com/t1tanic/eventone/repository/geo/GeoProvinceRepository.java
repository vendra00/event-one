package com.t1tanic.eventone.repository.geo;

import com.t1tanic.eventone.model.geo.GeoProvince;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GeoProvinceRepository extends JpaRepository<GeoProvince, String> {

    Optional<GeoProvince> findByCodeIgnoreCase(String code);

    List<GeoProvince> findAllByCommunity_CodeOrderByNameAsc(String communityCode);

    List<GeoProvince> findByCommunity_Code(String communityCode);

    Optional<GeoProvince> findByNameIgnoreCaseAndCommunity_Code(String name, String communityCode);

    List<GeoProvince> findAllByCodeIn(Iterable<String> codes);
}
