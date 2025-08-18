package com.t1tanic.eventone.model.geo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "geo_province", indexes=@Index(name="idx_prov_comm", columnList="community_code"))
@Getter
@Setter
public class GeoProvince {
    @Id
    @Column(length = 10) // ISO-3166-2 like ES-M
    private String code;
    @Column(nullable = false, length = 120)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "community_code", referencedColumnName = "code")
    private GeoCommunity community;
}
