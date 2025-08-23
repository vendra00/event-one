package com.t1tanic.eventone.model.mapper;

import com.t1tanic.eventone.model.*;
import com.t1tanic.eventone.model.dto.CuisineDto;
import com.t1tanic.eventone.model.dto.EventRequestDto;
import com.t1tanic.eventone.model.dto.GeoLocationDto;
import com.t1tanic.eventone.model.dto.request.CreateEventRequestReq;
import com.t1tanic.eventone.model.enums.EventRequestStatus;
import com.t1tanic.eventone.repository.CuisineRepository;
import com.t1tanic.eventone.service.geo.GeoResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EventRequestMapper {

    private final CuisineRepository cuisineRepo;
    private final GeoResolver geoResolver;

    public EventRequest from(CreateEventRequestReq req, AppUser consumer,
                             ProviderProfile provider, Offering offering) {
        var er = new EventRequest();
        er.setConsumer(consumer);
        er.setProvider(provider);
        er.setOffering(offering);
        er.setTitle(req.title());
        er.setStartsAt(req.startsAt());
        er.setEndsAt(req.endsAt());
        er.setGuests(req.guests());

        // cuisines: codes -> entities (strict on unknown codes)
        var codes = normalizeCodes(req.cuisineCodes());
        if (!codes.isEmpty()) {
            var found = cuisineRepo.findByCodeIn(codes);
            var foundCodes = found.stream().map(c -> c.getCode().toLowerCase()).collect(Collectors.toSet());
            var unknown = new LinkedHashSet<>(codes);
            unknown.removeAll(foundCodes);
            if (!unknown.isEmpty()) {
                throw new IllegalArgumentException("unknown_cuisine_codes:" + String.join(",", unknown));
                // If you prefer to ignore unknowns, replace with:
                // found.removeIf(c -> !codes.contains(c.getCode().toLowerCase()));
            }
            er.setCuisines(new HashSet<>(found));
        } else {
            er.setCuisines(new HashSet<>());
        }

        er.setServices(req.services());
        er.setBudgetCents(req.budgetCents());
        er.setCurrency(req.currency() != null ? req.currency() : "EUR");
        er.setNotes(req.notes());
        er.setStatus(EventRequestStatus.OPEN);

        // normalized geo
        er.setLocation(geoResolver.resolve(req.geo()));

        return er;
    }

    public EventRequestDto toDto(EventRequest e) {
        var loc = e.getLocation();
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

        // entities -> DTOs (sorted by name)
        var cuisineDtos =
                (e.getCuisines() == null ? List.<CuisineDto>of()
                        : e.getCuisines().stream()
                        .sorted(Comparator.comparing(Cuisine::getName))
                        .map(c -> new CuisineDto(c.getCode(), c.getName()))
                        .toList());

        return new EventRequestDto(
                e.getId(),
                e.getConsumer() != null ? e.getConsumer().getId() : null,
                e.getProvider() != null ? e.getProvider().getId() : null,
                e.getOffering() != null ? e.getOffering().getId() : null,
                e.getTitle(),
                e.getStartsAt(),
                e.getEndsAt(),
                e.getGuests(),
                locDto,
                cuisineDtos,
                e.getServices(),
                e.getBudgetCents(),
                e.getCurrency(),
                e.getNotes(),
                e.getStatus(),
                e.getCreatedAt()
        );
    }

    /** Normalize request codes: trim, lowercase, distinct, safe size. */
    private static List<String> normalizeCodes(List<String> in) {
        if (in == null) return List.of();
        return in.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toLowerCase)
                .distinct()
                .limit(20)
                .toList();
    }
}
