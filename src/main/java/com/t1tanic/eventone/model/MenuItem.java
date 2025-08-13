package com.t1tanic.eventone.model;

import com.t1tanic.eventone.model.enums.Course;
import jakarta.persistence.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor
@Entity @Table(name="menu_item")
public class MenuItem {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="offering_id", nullable=false)
    private Offering offering;

    @Column(nullable=false, length=200)
    private String name;

    @Column(length=500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=10)
    private Course course; // STARTER/MAIN/DESSERT/DRINK
}