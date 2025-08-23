package com.t1tanic.eventone.model.mapper;

import com.t1tanic.eventone.model.Cuisine;
import com.t1tanic.eventone.model.ProviderProfile;
import com.t1tanic.eventone.model.dto.CuisineDto;
import com.t1tanic.eventone.model.dto.GeoLocationDto;
import com.t1tanic.eventone.model.dto.ProviderProfileDto;
import com.t1tanic.eventone.model.dto.request.CreateProviderProfileReq;
import com.t1tanic.eventone.model.dto.request.GeoLocationInput;
import com.t1tanic.eventone.model.dto.request.UpdateProviderProfileReq;
import com.t1tanic.eventone.model.dto.request.UpsertMyProviderProfileReq;
import com.t1tanic.eventone.repository.CuisineRepository;
import com.t1tanic.eventone.service.geo.GeoResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProviderProfileMapper {

    private final GeoResolver geoResolver;
    private final CuisineRepository cuisineRepo;

    // ---------- Create ----------

    public ProviderProfile from(CreateProviderProfileReq req) {
        var p = new ProviderProfile();
        applyCreateFields(
                p,
                req.displayName(),
                req.kind(),
                req.bio(),
                req.geo(),
                req.minGuests(),
                req.maxGuests(),
                req.cuisineCodes(),
                req.services()
        );
        return p;
    }

    public ProviderProfile from(UpsertMyProviderProfileReq req) {
        var p = new ProviderProfile();
        applyCreateFields(
                p,
                req.displayName(),
                req.kind(),
                req.bio(),
                req.geo(),
                req.minGuests(),
                req.maxGuests(),
                req.cuisineCodes(),
                req.services()
        );
        return p;
    }

    private void applyCreateFields(ProviderProfile p,
                                   String displayName,
                                   com.t1tanic.eventone.model.enums.ProviderKind kind,
                                   String bio,
                                   GeoLocationInput geo,
                                   Integer minGuests,
                                   Integer maxGuests,
                                   List<String> cuisineCodes,
                                   String services) {

        p.setDisplayName(displayName);
        p.setKind(kind);
        p.setBio(trim(bio));
        p.setMinGuests(minGuests);
        p.setMaxGuests(maxGuests);
        p.setServices(trim(services));

        // cuisines: codes -> entities
        p.setCuisines(toCuisineSet(cuisineCodes));

        // normalized geo
        p.setLocation(geoResolver.resolve(geo));
    }

    // ---------- Update / Upsert ----------

    public void apply(UpdateProviderProfileReq req, ProviderProfile p) {
        if (req.displayName() != null) p.setDisplayName(req.displayName());
        if (req.kind() != null) p.setKind(req.kind());
        if (req.bio() != null) p.setBio(trim(req.bio()));
        if (req.minGuests() != null) p.setMinGuests(req.minGuests());
        if (req.maxGuests() != null) p.setMaxGuests(req.maxGuests());
        if (req.services() != null) p.setServices(trim(req.services()));

        // Replace cuisines only when provided
        if (req.cuisineCodes() != null) {
            p.setCuisines(toCuisineSet(req.cuisineCodes()));
        }

        if (req.geo() != null) {
            applyGeo(p, req.geo());
        }
    }

    public void apply(UpsertMyProviderProfileReq req, ProviderProfile p) {
        if (req.displayName() != null) p.setDisplayName(req.displayName());
        if (req.kind() != null) p.setKind(req.kind());
        if (req.bio() != null) p.setBio(trim(req.bio()));
        if (req.minGuests() != null) p.setMinGuests(req.minGuests());
        if (req.maxGuests() != null) p.setMaxGuests(req.maxGuests());
        if (req.services() != null) p.setServices(trim(req.services()));

        if (req.cuisineCodes() != null) {
            p.setCuisines(toCuisineSet(req.cuisineCodes()));
        }

        if (req.geo() != null) {
            applyGeo(p, req.geo());
        }
    }

    // ---------- To DTO ----------

    public ProviderProfileDto toDto(ProviderProfile p) {
        Long userId = (p.getUser() != null ? p.getUser().getId() : null);
        var loc = p.getLocation();

        GeoLocationDto locDto = null;
        if (loc != null) {
            locDto = new GeoLocationDto(
                    loc.getCountry() != null ? loc.getCountry().getCode() : null,
                    loc.getCountry() != null ? loc.getCountry().getName() : null,
                    loc.getCommunity() != null ? loc.getCommunity().getCode() : null,
                    loc.getCommunity() != null ? loc.getCommunity().getName() : null,
                    loc.getProvince() != null ? loc.getProvince().getCode() : null,
                    loc.getProvince() != null ? loc.getProvince().getName() : null,
                    loc.getMunicipality() != null ? loc.getMunicipality().getCode() : null,
                    loc.getMunicipality() != null ? loc.getMunicipality().getName() : null,
                    loc.getLocality(),
                    loc.getPostalCode()
            );
        }

        var cuisineDtos =
                p.getCuisines() == null ? List.<CuisineDto>of()
                        : p.getCuisines().stream()
                        .sorted(Comparator.comparing(Cuisine::getName))
                        .map(c -> new CuisineDto(c.getCode(), c.getName()))
                        .toList();

        return new ProviderProfileDto(
                p.getId(),
                userId,
                p.getDisplayName(),
                p.getKind(),
                p.getBio(),
                locDto,
                p.getMinGuests(),
                p.getMaxGuests(),
                cuisineDtos,
                p.getServices()
        );
    }

    // ---------- Helpers ----------

    private void applyGeo(ProviderProfile p, GeoLocationInput input) {
        var resolved = geoResolver.resolve(input);
        var current = p.getLocation();
        if (current == null) {
            p.setLocation(resolved);
            return;
        }
        boolean hasComm = hasText(input.communityCode()) || hasText(input.communityName());
        boolean hasProv = hasText(input.provinceCode()) || hasText(input.provinceName());
        boolean hasMuni = hasText(input.municipalityCode()) || hasText(input.municipalityName());

        if (hasComm) {
            current.setCommunity(resolved.getCommunity());
            current.setProvince(null);
            current.setMunicipality(null);
        }
        if (hasProv) {
            current.setProvince(resolved.getProvince());
            current.setMunicipality(null);
        }
        if (hasMuni) {
            current.setMunicipality(resolved.getMunicipality());
        }

        if (input.locality() != null) current.setLocality(trim(input.locality()));
        if (input.postalCode() != null) current.setPostalCode(input.postalCode());
    }

    private Set<Cuisine> toCuisineSet(List<String> codes) {
        if (codes == null || codes.isEmpty()) return new HashSet<>();
        var norm = codes.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toLowerCase)
                .distinct()
                .limit(30)
                .toList();
        var found = cuisineRepo.findByCodeIn(norm);
        var foundCodes = found.stream().map(c -> c.getCode().toLowerCase()).collect(Collectors.toSet());
        var unknown = new LinkedHashSet<>(norm);
        unknown.removeAll(foundCodes);
        if (!unknown.isEmpty()) {
            throw new IllegalArgumentException("unknown_cuisine_codes:" + String.join(",", unknown));
        }
        return new HashSet<>(found);
    }

    private static boolean hasText(String s) { return StringUtils.hasText(s); }
    private static String trim(String s) { return s == null ? null : s.trim(); }
}
