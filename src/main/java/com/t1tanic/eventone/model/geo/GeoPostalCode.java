// model/geo/GeoPostalCode.java
package com.t1tanic.eventone.model.geo;

import jakarta.persistence.*;
import lombok.Getter; import lombok.Setter;

@Getter @Setter
@Entity @Table(name="geo_postal_code")
public class GeoPostalCode {
    @Id
    @Column(length = 5)
    private String code; // e.g., 28001

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "municipality_code", nullable = false)
    private GeoMunicipality municipality;
}
