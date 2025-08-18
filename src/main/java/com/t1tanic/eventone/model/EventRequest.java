package com.t1tanic.eventone.model;

import com.t1tanic.eventone.model.enums.EventRequestStatus;
import com.t1tanic.eventone.model.geo.GeoLocation;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor
@Entity
@Table(name = "event_request", indexes = {
        @Index(name = "idx_evreq_time", columnList = "starts_at,ends_at"),
        @Index(name = "idx_evreq_consumer", columnList = "consumer_id"),
        @Index(name = "idx_evreq_provider", columnList = "provider_id"),
        // New geo indexes for fast filtering
        @Index(name = "idx_evreq_geo_comm", columnList = "geo_community_code"),
        @Index(name = "idx_evreq_geo_prov", columnList = "geo_province_code"),
        @Index(name = "idx_evreq_geo_muni", columnList = "geo_municipality_code"),
        @Index(name = "idx_evreq_geo_postal", columnList = "geo_postal_code")
})
public class EventRequest {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "consumer_id", nullable = false)
    private AppUser consumer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id")
    private ProviderProfile provider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offering_id")
    private Offering offering;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "starts_at", nullable = false)
    private LocalDateTime startsAt;

    @Column(name = "ends_at", nullable = false)
    private LocalDateTime endsAt;

    // REPLACES old `city` and `region`
    @Embedded
    private GeoLocation location = new GeoLocation();

    @Column(nullable = false)
    private Integer guests;

    @Column(length = 400) private String cuisines; // tags MVP
    @Column(length = 400) private String services; // tags MVP

    private Integer budgetCents;

    @Column(length = 3, nullable = false)
    private String currency = "EUR";

    @Column(columnDefinition = "text")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private EventRequestStatus status = EventRequestStatus.OPEN;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}