package com.t1tanic.eventone.model;

import com.t1tanic.eventone.model.enums.EventRequestStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor
@Entity @Table(name="event_request", indexes = {
        @Index(name="idx_evreq_date", columnList="date")
})
public class EventRequest {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="user_id", nullable=false)
    private AppUser user;

    @Column(nullable=false, length=200)
    private String title;

    @Column(name="date", nullable=false)
    private LocalDateTime date;

    @Column(length=120) private String city;
    @Column(length=120) private String region;

    @Column(nullable=false)
    private Integer guests;

    @Column(length=400) private String cuisines; // tags
    @Column(length=400) private String services; // tags

    private Integer budgetCents;

    @Column(length=3, nullable=false)
    private String currency = "EUR";

    @Column(columnDefinition="text")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=8)
    private EventRequestStatus status = EventRequestStatus.OPEN;
}
