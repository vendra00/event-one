package com.t1tanic.eventone.model.mapper;

import com.t1tanic.eventone.model.AvailabilitySlot;
import com.t1tanic.eventone.model.ProviderProfile;
import com.t1tanic.eventone.model.dto.AvailabilitySlotDto;
import com.t1tanic.eventone.model.dto.request.CreateAvailabilityReq;
import com.t1tanic.eventone.model.dto.request.UpdateAvailabilityReq;
import org.springframework.stereotype.Component;

@Component
public class AvailabilityMapper {

    public AvailabilitySlot from(CreateAvailabilityReq req, ProviderProfile provider) {
        var s = new AvailabilitySlot();
        s.setProvider(provider);
        s.setStartsAt(req.startsAt());
        s.setEndsAt(req.endsAt());
        s.setNote(req.note());
        return s;
    }

    public void apply(UpdateAvailabilityReq req, AvailabilitySlot s) {
        if (req.startsAt() != null) s.setStartsAt(req.startsAt());
        if (req.endsAt() != null) s.setEndsAt(req.endsAt());
        if (req.note() != null) s.setNote(req.note());
    }

    public AvailabilitySlotDto toDto(AvailabilitySlot s) {
        return new AvailabilitySlotDto(
                s.getId(),
                s.getProvider().getId(),
                s.getStartsAt(),
                s.getEndsAt(),
                s.getNote()
        );
    }
}
