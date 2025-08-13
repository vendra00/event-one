package com.t1tanic.eventone.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter @NoArgsConstructor
@Entity @Table(name="offering", indexes = {
        @Index(name="idx_offering_city_region", columnList="city,region")
})
public class Offering {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="provider_id", nullable=false)
    private ProviderProfile provider;

    @Column(nullable=false, length=200)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name="base_price_cents", nullable=false)
    private Integer basePriceCents;

    @Column(length=3, nullable=false)
    private String currency = "EUR";

    private Integer minGuests;
    private Integer maxGuests;

    @Column(length=400) private String cuisines;
    @Column(length=400) private String services;

    @Column(length=120) private String city;
    @Column(length=120) private String region;

    @Column(nullable=false, columnDefinition = "tinyint(1)")
    private boolean active = true;

    @OneToMany(mappedBy="offering", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<MenuItem> menu = new ArrayList<>();
}
