package com.t1tanic.eventone.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor
@Entity @Table(name="availability_slot")
public class AvailabilitySlot {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="provider_id", nullable=false)
    private ProviderProfile provider;

    @Column(name="starts_at", nullable=false)
    private LocalDateTime startsAt;

    @Column(name="ends_at", nullable=false)
    private LocalDateTime endsAt;

    @Column(length=255)
    private String note;
}