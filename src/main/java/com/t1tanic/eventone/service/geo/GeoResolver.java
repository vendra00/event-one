package com.t1tanic.eventone.service.geo;

import com.t1tanic.eventone.model.geo.GeoCommunity;
import com.t1tanic.eventone.model.geo.GeoLocation;
import com.t1tanic.eventone.model.dto.request.GeoLocationInput;
import com.t1tanic.eventone.model.geo.GeoMunicipality;
import com.t1tanic.eventone.model.geo.GeoProvince;
import com.t1tanic.eventone.repository.geo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class GeoResolver {

    private final GeoCountryRepository countries;
    private final GeoCommunityRepository communities;
    private final GeoProvinceRepository provinces;
    private final GeoMunicipalityRepository municipalities;

    // Spain-only default
    private static final String DEFAULT_COUNTRY = "ES";

    public GeoLocation resolve(GeoLocationInput in) {
        if (in == null) return null;

        var gl = new GeoLocation();

        // Country fixed to ES
        var country = countries.findById(DEFAULT_COUNTRY)
                .orElseThrow(() -> new IllegalStateException("country_not_seeded: " + DEFAULT_COUNTRY));
        gl.setCountry(country);

        // --- Community ---
        GeoCommunity community = null;
        if (hasText(in.communityCode())) {
            community = communities.findById(in.communityCode().trim())
                    .orElseThrow(() -> new IllegalArgumentException("community_not_found:" + in.communityCode()));
        } else if (hasText(in.communityName())) {
            community = communities.findByNameIgnoreCaseAndCountry_Code(in.communityName().trim(), DEFAULT_COUNTRY)
                    .orElseThrow(() -> new IllegalArgumentException("community_not_found_by_name:" + in.communityName()));
        }
        gl.setCommunity(community);

        // --- Province (prefer code; else name within community) ---
        GeoProvince province = null;
        if (hasText(in.provinceCode())) {
            province = provinces.findById(in.provinceCode().trim())
                    .orElseThrow(() -> new IllegalArgumentException("province_not_found:" + in.provinceCode()));
        } else if (hasText(in.provinceName()) && community != null) {
            province = provinces.findByNameIgnoreCaseAndCommunity_Code(in.provinceName().trim(), community.getCode())
                    .orElseThrow(() -> new IllegalArgumentException("province_not_found_by_name:" + in.provinceName()));
        }
        gl.setProvince(province);

        // --- Municipality (prefer code; else name within province) ---
        GeoMunicipality municipality = null;
        if (hasText(in.municipalityCode())) {
            municipality = municipalities.findById(in.municipalityCode().trim())
                    .orElseThrow(() -> new IllegalArgumentException("municipality_not_found:" + in.municipalityCode()));
        } else if (hasText(in.municipalityName()) && province != null) {
            municipality = municipalities.findByNameIgnoreCaseAndProvince_Code(in.municipalityName().trim(), province.getCode())
                    .orElseThrow(() -> new IllegalArgumentException("municipality_not_found_by_name:" + in.municipalityName()));
        }
        gl.setMunicipality(municipality);

        // Optional
        gl.setLocality(trim(in.locality()));
        gl.setPostalCode(in.postalCode());

        return gl;
    }

    private static boolean hasText(String s) { return StringUtils.hasText(s); }
    private static String trim(String s) { return s == null ? null : s.trim(); }
}
