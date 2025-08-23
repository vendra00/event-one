package com.t1tanic.eventone.model;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "cuisine",
        uniqueConstraints = @UniqueConstraint(name = "uk_cuisine_code", columnNames = "code"),
        indexes = {
                @Index(name = "idx_cuisine_name", columnList = "name"),
                @Index(name = "idx_cuisine_active", columnList = "active")
        })
public class Cuisine {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64)
    private String code;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false)
    private boolean active = true;
}
