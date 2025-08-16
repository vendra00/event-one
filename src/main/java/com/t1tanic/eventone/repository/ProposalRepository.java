package com.t1tanic.eventone.repository;

import com.t1tanic.eventone.model.Proposal;
import com.t1tanic.eventone.model.enums.ProposalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProposalRepository extends JpaRepository<Proposal, Long> {
    Page<Proposal> findByProviderId(Long providerId, Pageable pageable);
    Page<Proposal> findByRequestId(Long requestId, Pageable pageable);
    List<Proposal> findByRequestId(Long requestId);
    long countByRequestIdAndStatus(Long requestId, ProposalStatus status);
}
