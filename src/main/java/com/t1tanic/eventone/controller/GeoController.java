package com.t1tanic.eventone.controller;

import com.t1tanic.eventone.model.dto.GeoOptionDto;
import com.t1tanic.eventone.repository.geo.GeoCommunityRepository;
import com.t1tanic.eventone.repository.geo.GeoMunicipalityRepository;
import com.t1tanic.eventone.repository.geo.GeoProvinceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/geo")
@RequiredArgsConstructor
public class GeoController {

    private static final String COUNTRY_ES = "ES";
    private static final int DEFAULT_LIMIT = 50;

    private final GeoCommunityRepository communities;
    private final GeoProvinceRepository provinces;
    private final GeoMunicipalityRepository municipalities;

    @PreAuthorize("hasAnyRole('PROVIDER','CONSUMER', 'ADMIN')")
    @GetMapping("/communities")
    public List<GeoOptionDto> communities(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "limit", required = false) Integer limit
    ) {
        int cap = limit != null && limit > 0 ? Math.min(limit, 200) : DEFAULT_LIMIT;
        var list = communities.findAllByCountry_CodeOrderByNameAsc(COUNTRY_ES);
        Stream<GeoOptionDto> stream = list.stream()
                .map(c -> new GeoOptionDto(c.getCode(), c.getName()));
        if (StringUtils.hasText(q)) {
            String needle = q.trim().toLowerCase();
            stream = stream.filter(o -> o.name().toLowerCase().contains(needle));
        }
        return stream
                .sorted(Comparator.comparing(GeoOptionDto::name))
                .limit(cap)
                .toList();
    }

    @PreAuthorize("hasAnyRole('PROVIDER','CONSUMER', 'ADMIN')")
    @GetMapping("/provinces")
    public List<GeoOptionDto> provinces(
            @RequestParam("communityCode") String communityCode,
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "limit", required = false) Integer limit
    ) {
        int cap = limit != null && limit > 0 ? Math.min(limit, 200) : DEFAULT_LIMIT;
        var list = provinces.findAllByCommunity_CodeOrderByNameAsc(communityCode);
        Stream<GeoOptionDto> stream = list.stream()
                .map(p -> new GeoOptionDto(p.getCode(), p.getName()));
        if (StringUtils.hasText(q)) {
            String needle = q.trim().toLowerCase();
            stream = stream.filter(o -> o.name().toLowerCase().contains(needle));
        }
        return stream
                .sorted(Comparator.comparing(GeoOptionDto::name))
                .limit(cap)
                .toList();
    }

    @PreAuthorize("hasAnyRole('PROVIDER','CONSUMER', 'ADMIN')")
    @GetMapping("/municipalities")
    public List<GeoOptionDto> municipalities(
            @RequestParam("provinceCode") String provinceCode,
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "limit", required = false) Integer limit
    ) {
        int cap = limit != null && limit > 0 ? Math.min(limit, 200) : DEFAULT_LIMIT;

        if (StringUtils.hasText(q)) {
            // Use indexed search when querying with text (fast + capped)
            var list = municipalities.findTop20ByNameContainingIgnoreCaseAndProvince_CodeOrderByNameAsc(q.trim(), provinceCode);
            return list.stream()
                    .map(m -> new GeoOptionDto(m.getCode(), m.getName()))
                    .limit(cap)
                    .toList();
        } else {
            // Full list for the selected province
            var list = municipalities.findAllByProvince_CodeOrderByNameAsc(provinceCode);
            return list.stream()
                    .map(m -> new GeoOptionDto(m.getCode(), m.getName()))
                    .limit(cap)
                    .toList();
        }
    }
}
