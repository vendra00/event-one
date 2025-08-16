package com.t1tanic.eventone.model.mapper;

import com.t1tanic.eventone.model.Booking;
import com.t1tanic.eventone.model.dto.BookingDto;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {
    public BookingDto toDto(Booking b) {
        return new BookingDto(
                b.getId(),
                b.getProposal().getId(),
                b.getProposal().getRequest().getId(),
                b.getProposal().getProvider().getId(),
                b.getProposal().getRequest().getConsumer().getId(),
                b.getEventDate(),
                b.getHeadcount(),
                b.getStatus()
        );
    }
}
