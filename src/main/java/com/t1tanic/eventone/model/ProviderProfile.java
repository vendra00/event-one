package com.t1tanic.eventone.model;

import com.t1tanic.eventone.model.enums.ProviderKind;
import com.t1tanic.eventone.model.geo.GeoLocation; // <— embeddable with @ManyToOne refs
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter @NoArgsConstructor
@Entity
@Table(name = "provider_profile",
        indexes = {
                @Index(name = "idx_provider_user", columnList = "user_id", unique = true),
                // helpful if you’ll filter providers by area later
                @Index(name = "idx_provider_geo_comm", columnList = "geo_community_code"),
                @Index(name = "idx_provider_geo_prov", columnList = "geo_province_code"),
                @Index(name = "idx_provider_geo_muni", columnList = "geo_municipality_code")
        })
public class ProviderProfile {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private AppUser user;

    @Column(name = "display_name", nullable = false, length = 200)
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private ProviderKind kind;

    @Column(columnDefinition = "text")
    private String bio;

    // --- Normalized geo (preferred going forward) ---
    // Uses same column names as EventRequest for consistency, but on a different table.
    @Embedded
    private GeoLocation location;

    // --- Legacy fields (kept temporarily for fallback queries / migration) ---
    @Column(length = 120) private String city;
    @Column(length = 120) private String region;
    @Column(length = 2)   private String country; // ISO-3166-1 alpha-2

    private Integer minGuests;
    private Integer maxGuests;

    @Column(length = 400) private String cuisines; // MVP tags: "italian,bbq"
    @Column(length = 400) private String services; // MVP tags: "waiters,bar"

    @OneToMany(mappedBy = "provider", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AvailabilitySlot> availabilities = new ArrayList<>();
}
