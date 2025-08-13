package com.t1tanic.eventone.model.mapper;

import com.t1tanic.eventone.model.ProviderProfile;
import com.t1tanic.eventone.model.dto.request.CreateProviderProfileReq;
import com.t1tanic.eventone.model.dto.ProviderProfileDto;
import com.t1tanic.eventone.model.dto.request.UpdateProviderProfileReq;
import com.t1tanic.eventone.model.dto.request.UpsertMyProviderProfileReq;
import org.springframework.stereotype.Component;

@Component
public class ProviderProfileMapper {

    public ProviderProfile from(CreateProviderProfileReq req) {
        var p = new ProviderProfile();
        applyCreateFields(p, req.displayName(), req.kind(), req.bio(), req.city(), req.region(),
                req.country(), req.minGuests(), req.maxGuests(), req.cuisines(), req.services());
        return p;
    }

    // For /me creation (no userId in the DTO)
    public ProviderProfile from(UpsertMyProviderProfileReq req) {
        var p = new ProviderProfile();
        applyCreateFields(p, req.displayName(), req.kind(), req.bio(), req.city(), req.region(),
                req.country(), req.minGuests(), req.maxGuests(), req.cuisines(), req.services());
        return p;
    }

    public void apply(UpdateProviderProfileReq req, ProviderProfile p) {
        if (req.displayName() != null) p.setDisplayName(req.displayName());
        if (req.kind() != null) p.setKind(req.kind());
        if (req.bio() != null) p.setBio(trim(req.bio()));
        if (req.city() != null) p.setCity(trim(req.city()));
        if (req.region() != null) p.setRegion(trim(req.region()));
        if (req.country() != null) p.setCountry(normCountry(req.country()));
        if (req.minGuests() != null) p.setMinGuests(req.minGuests());
        if (req.maxGuests() != null) p.setMaxGuests(req.maxGuests());
        if (req.cuisines() != null) p.setCuisines(trim(req.cuisines()));
        if (req.services() != null) p.setServices(trim(req.services()));
    }

    // For /me upsert (apply partial fields)
    public void apply(UpsertMyProviderProfileReq req, ProviderProfile p) {
        if (req.displayName() != null) p.setDisplayName(req.displayName());
        if (req.kind() != null) p.setKind(req.kind());
        if (req.bio() != null) p.setBio(trim(req.bio()));
        if (req.city() != null) p.setCity(trim(req.city()));
        if (req.region() != null) p.setRegion(trim(req.region()));
        if (req.country() != null) p.setCountry(normCountry(req.country()));
        if (req.minGuests() != null) p.setMinGuests(req.minGuests());
        if (req.maxGuests() != null) p.setMaxGuests(req.maxGuests());
        if (req.cuisines() != null) p.setCuisines(trim(req.cuisines()));
        if (req.services() != null) p.setServices(trim(req.services()));
    }

    public ProviderProfileDto toDto(ProviderProfile p) {
        Long userId = (p.getUser() != null ? p.getUser().getId() : null);
        return new ProviderProfileDto(
                p.getId(), userId, p.getDisplayName(), p.getKind(), p.getBio(),
                p.getCity(), p.getRegion(), p.getCountry(),
                p.getMinGuests(), p.getMaxGuests(), p.getCuisines(), p.getServices()
        );
    }

    private static void applyCreateFields(ProviderProfile p, String displayName, Object kind, String bio,
                                          String city, String region, String country,
                                          Integer minGuests, Integer maxGuests,
                                          String cuisines, String services) {
        p.setDisplayName(displayName);
        p.setKind(kind == null ? null : (com.t1tanic.eventone.model.enums.ProviderKind) kind);
        p.setBio(trim(bio));
        p.setCity(trim(city));
        p.setRegion(trim(region));
        p.setCountry(normCountry(country));
        p.setMinGuests(minGuests);
        p.setMaxGuests(maxGuests);
        p.setCuisines(trim(cuisines));
        p.setServices(trim(services));
    }

    private static String trim(String s) { return s == null ? null : s.trim(); }
    private static String normCountry(String s) { return s == null ? null : s.trim().toUpperCase(); }
}
