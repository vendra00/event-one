package com.t1tanic.eventone.model.mapper;

import com.t1tanic.eventone.model.*;
import com.t1tanic.eventone.model.dto.EventRequestDto;
import com.t1tanic.eventone.model.dto.request.CreateEventRequestReq;
import com.t1tanic.eventone.model.dto.GeoLocationDto;
import com.t1tanic.eventone.model.enums.EventRequestStatus;
import com.t1tanic.eventone.repository.geo.GeoCommunityRepository;
import com.t1tanic.eventone.repository.geo.GeoCountryRepository;
import com.t1tanic.eventone.repository.geo.GeoMunicipalityRepository;
import com.t1tanic.eventone.repository.geo.GeoProvinceRepository;
import com.t1tanic.eventone.service.geo.GeoResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventRequestMapper {

    private final GeoCountryRepository countries;
    private final GeoCommunityRepository communities;
    private final GeoProvinceRepository provinces;
    private final GeoMunicipalityRepository municipalities;

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
        er.setCuisines(req.cuisines());
        er.setServices(req.services());
        er.setBudgetCents(req.budgetCents());
        er.setCurrency(req.currency() != null ? req.currency() : "EUR");
        er.setNotes(req.notes());
        er.setStatus(EventRequestStatus.OPEN);

        er.setLocation(geoResolver.resolve(req.geo())); // <-- single call

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
                e.getCuisines(),
                e.getServices(),
                e.getBudgetCents(),
                e.getCurrency(),
                e.getNotes(),
                e.getStatus(),
                e.getCreatedAt()
        );
    }
}
