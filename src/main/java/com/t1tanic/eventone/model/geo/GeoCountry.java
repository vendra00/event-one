package com.t1tanic.eventone.model.geo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "geo_country")
@Getter
@Setter
public class GeoCountry {
    @Id
    @Column(length = 2)
    private String code; // ISO-3166-1 alpha-2 (e.g., ES)
    @Column(nullable = false, length = 120)
    private String name;
}
