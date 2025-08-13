package com.t1tanic.eventone.repository;

import com.t1tanic.eventone.model.Proposal;
import com.t1tanic.eventone.model.enums.ProposalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProposalRepository extends JpaRepository<Proposal, Long> {
    Page<Proposal> findByProviderIdAndStatus(Long providerId, ProposalStatus status, Pageable pageable);
    Page<Proposal> findByEventRequestId(Long eventRequestId, Pageable pageable);
    boolean existsByEventRequestIdAndProviderId(Long eventRequestId, Long providerId);
}
