package com.t1tanic.eventone.model.geo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "geo_municipality", indexes=@Index(name="idx_muni_prov", columnList="province_code"))
@Getter
@Setter
public class GeoMunicipality {

    @Id
    @Column(length = 10) // INE 5-digit; pad/format as string
    private String code;
    @Column(nullable = false, length = 120)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "province_code", referencedColumnName = "code")
    private GeoProvince province;
}
