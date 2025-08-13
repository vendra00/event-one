package com.t1tanic.eventone.model;

import com.t1tanic.eventone.model.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor
@Entity @Table(name="booking")
public class Booking {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="proposal_id", nullable=false, unique=true)
    private Proposal proposal;

    @Column(name="event_date", nullable=false)
    private LocalDateTime eventDate;

    @Column(name="headcount", nullable=false)
    private Integer headcount;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=10)
    private BookingStatus status = BookingStatus.PENDING;
}