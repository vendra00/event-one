package com.t1tanic.eventone.repository.geo;

import com.t1tanic.eventone.model.geo.GeoMunicipality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GeoMunicipalityRepository extends JpaRepository<GeoMunicipality, String> {

    Optional<GeoMunicipality> findByCodeIgnoreCase(String code);

    // Typical drill-downs
    List<GeoMunicipality> findAllByProvince_CodeOrderByNameAsc(String provinceCode);

    List<GeoMunicipality> findByProvince_Code(String provinceCode);

    // Fast name lookups for search/autocomplete within a province
    Optional<GeoMunicipality> findByNameIgnoreCaseAndProvince_Code(String name, String provinceCode);

    List<GeoMunicipality> findTop20ByNameContainingIgnoreCaseAndProvince_CodeOrderByNameAsc(String q, String provinceCode);

    // (Optional) Through the province -> community
    List<GeoMunicipality> findByProvince_Community_Code(String communityCode);

    List<GeoMunicipality> findAllByCodeIn(Iterable<String> codes);
}
