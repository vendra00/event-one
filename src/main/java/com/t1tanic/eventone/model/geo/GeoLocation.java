package com.t1tanic.eventone.model.geo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class GeoLocation {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "geo_country_code", referencedColumnName = "code")
    private GeoCountry country; // code: "ES"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "geo_community_code", referencedColumnName = "code")
    private GeoCommunity community; // e.g., ES-AN

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "geo_province_code", referencedColumnName = "code")
    private GeoProvince province;  // e.g., ES-M

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "geo_municipality_code", referencedColumnName = "code")
    private GeoMunicipality municipality; // INE 5-digit

    @Column(name = "geo_locality", length = 120)
    private String locality;

    @Column(name = "geo_postal_code", length = 5)
    private String postalCode;
}
