package com.t1tanic.eventone.model;

import com.t1tanic.eventone.model.enums.ProposalStatus;
import jakarta.persistence.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor
@Entity @Table(name="proposal")
public class Proposal {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="event_request_id", nullable=false)
    private EventRequest eventRequest;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="provider_id", nullable=false)
    private ProviderProfile provider;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="offering_id")
    private Offering offering;

    @Column(name="price_cents", nullable=false)
    private Integer priceCents;

    @Column(length=3, nullable=false)
    private String currency = "EUR";

    @Column(columnDefinition="text")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=10)
    private ProposalStatus status = ProposalStatus.SENT;
}
