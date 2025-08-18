package com.t1tanic.eventone.model.mapper;

import com.t1tanic.eventone.model.ProviderProfile;
import com.t1tanic.eventone.model.dto.ProviderProfileDto;
import com.t1tanic.eventone.model.dto.GeoLocationDto;
import com.t1tanic.eventone.model.dto.request.CreateProviderProfileReq;
import com.t1tanic.eventone.model.dto.request.UpdateProviderProfileReq;
import com.t1tanic.eventone.model.dto.request.UpsertMyProviderProfileReq;
import com.t1tanic.eventone.model.dto.request.GeoLocationInput;
import com.t1tanic.eventone.model.geo.GeoLocation;
import com.t1tanic.eventone.service.geo.GeoResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class ProviderProfileMapper {

    private final GeoResolver geoResolver; // <— central geo resolution (Spain-only for now)

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
                req.cuisines(),
                req.services()
        );
        return p;
    }

    // For /me creation (no userId in the DTO)
    public ProviderProfile from(UpsertMyProviderProfileReq req) {
        var p = new ProviderProfile();
        applyCreateFields(
                p,
                req.displayName(),
                req.kind(),       // may be null here (controller enforces on create)
                req.bio(),
                req.geo(),
                req.minGuests(),
                req.maxGuests(),
                req.cuisines(),
                req.services()
        );
        return p;
    }

    private void applyCreateFields(ProviderProfile p,
                                   String displayName,
                                   Object kind,
                                   String bio,
                                   GeoLocationInput geo,
                                   Integer minGuests,
                                   Integer maxGuests,
                                   String cuisines,
                                   String services) {

        p.setDisplayName(displayName);
        p.setKind(kind == null ? null : (com.t1tanic.eventone.model.enums.ProviderKind) kind);
        p.setBio(trim(bio));
        p.setMinGuests(minGuests);
        p.setMaxGuests(maxGuests);
        p.setCuisines(trim(cuisines));
        p.setServices(trim(services));

        // normalized geo via resolver (Spain default country inside resolver)
        p.setLocation(geoResolver.resolve(geo));

        // legacy fields removed
        p.setCity(null);
        p.setRegion(null);
        p.setCountry(null);
    }

    // ---------- Update / Upsert ----------

    public void apply(UpdateProviderProfileReq req, ProviderProfile p) {
        if (req.displayName() != null) p.setDisplayName(req.displayName());
        if (req.kind() != null) p.setKind(req.kind());
        if (req.bio() != null) p.setBio(trim(req.bio()));
        if (req.minGuests() != null) p.setMinGuests(req.minGuests());
        if (req.maxGuests() != null) p.setMaxGuests(req.maxGuests());
        if (req.cuisines() != null) p.setCuisines(trim(req.cuisines()));
        if (req.services() != null) p.setServices(trim(req.services()));

        if (req.geo() != null) {
            applyGeo(p, req.geo());
        }
    }

    // For /me upsert (apply partial fields)
    public void apply(UpsertMyProviderProfileReq req, ProviderProfile p) {
        if (req.displayName() != null) p.setDisplayName(req.displayName());
        if (req.kind() != null) p.setKind(req.kind());
        if (req.bio() != null) p.setBio(trim(req.bio()));
        if (req.minGuests() != null) p.setMinGuests(req.minGuests());
        if (req.maxGuests() != null) p.setMaxGuests(req.maxGuests());
        if (req.cuisines() != null) p.setCuisines(trim(req.cuisines()));
        if (req.services() != null) p.setServices(trim(req.services()));

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

        return new ProviderProfileDto(
                p.getId(),
                userId,
                p.getDisplayName(),
                p.getKind(),
                p.getBio(),
                locDto,
                p.getMinGuests(),
                p.getMaxGuests(),
                p.getCuisines(),
                p.getServices()
        );
    }

    // ---------- Helpers ----------

    private void applyGeo(ProviderProfile p, GeoLocationInput input) {
        // Resolve whatever was provided (codes or names, possibly partial)
        var resolved = geoResolver.resolve(input);

        // Merge into existing location; only overwrite levels the client provided.
        var current = p.getLocation();
        if (current == null) {
            p.setLocation(resolved);
            return;
        }

        // Detect which levels were explicitly provided (by code or name)
        boolean hasComm = hasText(input.communityCode()) || hasText(input.communityName());
        boolean hasProv = hasText(input.provinceCode()) || hasText(input.provinceName());
        boolean hasMuni = hasText(input.municipalityCode()) || hasText(input.municipalityName());

        if (hasComm) {
            current.setCommunity(resolved.getCommunity());
            // when community changes, downstream hierarchy should be reconsidered
            current.setProvince(null);
            current.setMunicipality(null);
        }
        if (hasProv) {
            current.setProvince(resolved.getProvince());
            // when province changes, clear municipality
            current.setMunicipality(null);
        }
        if (hasMuni) {
            current.setMunicipality(resolved.getMunicipality());
        }

        // Optional locality/postal updates
        if (input.locality() != null) current.setLocality(trim(input.locality()));
        if (input.postalCode() != null) current.setPostalCode(input.postalCode());
    }

    private static boolean hasText(String s) { return StringUtils.hasText(s); }
    private static String trim(String s) { return s == null ? null : s.trim(); }
}
