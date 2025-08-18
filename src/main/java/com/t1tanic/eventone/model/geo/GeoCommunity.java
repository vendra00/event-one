package com.t1tanic.eventone.model.geo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "geo_community")
@Getter
@Setter
public class GeoCommunity {
    @Id
    @Column(length = 10) // ISO-3166-2 like ES-AN
    private String code;
    @Column(nullable = false, length = 120)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "country_code", referencedColumnName = "code")
    private GeoCountry country;
}
