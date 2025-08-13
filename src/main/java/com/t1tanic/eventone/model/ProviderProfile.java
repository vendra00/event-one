package com.t1tanic.eventone.model;

import com.t1tanic.eventone.model.enums.ProviderKind;
import jakarta.persistence.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor
@Entity @Table(name="provider_profile")
public class ProviderProfile {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="user_id", nullable=false, unique=true)
    private AppUser user;

    @Column(name="display_name", nullable=false, length=200)
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=12)
    private ProviderKind kind;

    @Column(columnDefinition="text")
    private String bio;

    @Column(length=120) private String city;
    @Column(length=120) private String region;
    @Column(length=2)   private String country; // ISO-3166-1 alpha-2

    private Integer minGuests;
    private Integer maxGuests;

    @Column(length=400) private String cuisines; // MVP tags: "italian,bbq"
    @Column(length=400) private String services; // MVP tags: "waiters,bar"
}
