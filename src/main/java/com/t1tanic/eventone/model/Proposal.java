package com.t1tanic.eventone.model;

import com.t1tanic.eventone.model.enums.ProposalStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor
@Entity @Table(name="proposal",
        indexes = {
                @Index(name="idx_proposal_request", columnList="request_id"),
                @Index(name="idx_proposal_provider", columnList="provider_id")
        }
)
public class Proposal {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="request_id", nullable=false)
    private EventRequest request;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="provider_id", nullable=false)
    private ProviderProfile provider;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="offering_id")
    private Offering offering; // optional

    @Column(name="price_cents", nullable=false)
    private Integer priceCents;

    @Column(length=3, nullable=false)
    private String currency = "EUR";

    @Column(length=1000)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=10)
    private ProposalStatus status = ProposalStatus.SENT;

    @Column(name="created_at", nullable=false, updatable=false)
    private LocalDateTime createdAt;

    @Column(name="updated_at", nullable=false)
    private LocalDateTime updatedAt;

    @PrePersist void prePersist() {
        var now = LocalDateTime.now();
        createdAt = now; updatedAt = now;
    }
    @PreUpdate void preUpdate() { updatedAt = LocalDateTime.now(); }
}
