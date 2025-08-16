
package com.t1tanic.eventone.model.mapper;

import com.t1tanic.eventone.model.*;
import com.t1tanic.eventone.model.dto.EventRequestDto;
import com.t1tanic.eventone.model.dto.request.CreateEventRequestReq;
import com.t1tanic.eventone.model.enums.EventRequestStatus;
import org.springframework.stereotype.Component;

@Component
public class EventRequestMapper {

    public EventRequest from(CreateEventRequestReq req, AppUser consumer,
                             ProviderProfile provider, Offering offering) {
        var er = new EventRequest();
        er.setConsumer(consumer);
        er.setProvider(provider);   // may be null
        er.setOffering(offering);   // may be null
        er.setTitle(req.title());
        er.setStartsAt(req.startsAt());
        er.setEndsAt(req.endsAt());
        er.setGuests(req.guests());
        er.setCity(req.city());
        er.setRegion(req.region());
        er.setCuisines(req.cuisines());
        er.setServices(req.services());
        er.setBudgetCents(req.budgetCents());
        er.setCurrency(req.currency() != null ? req.currency() : "EUR");
        er.setNotes(req.notes());
        er.setStatus(EventRequestStatus.OPEN);
        return er;
    }

    public EventRequestDto toDto(EventRequest e) {
        return new EventRequestDto(
                e.getId(),
                e.getConsumer() != null ? e.getConsumer().getId() : null,
                e.getProvider() != null ? e.getProvider().getId() : null,
                e.getOffering() != null ? e.getOffering().getId() : null,
                e.getTitle(),
                e.getStartsAt(),
                e.getEndsAt(),
                e.getGuests(),
                e.getCity(),
                e.getRegion(),
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
