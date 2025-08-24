package com.t1tanic.eventone.service;

import com.t1tanic.eventone.model.dto.ProposalDto;
import com.t1tanic.eventone.model.dto.request.proposal.CreateProposalReq;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProposalService {
    ProposalDto create(Long providerUserId, Long requestId, CreateProposalReq req);
    Page<ProposalDto> listForProvider(Long providerUserId, Pageable pageable);
    Page<ProposalDto> listForRequestAsConsumer(Long consumerUserId, Long requestId, Pageable pageable);
    ProposalDto getForProvider(Long providerUserId, Long proposalId);
    ProposalDto getForConsumer(Long consumerUserId, Long proposalId);
    void withdraw(Long providerUserId, Long proposalId);
    // accept returns a Booking
    com.t1tanic.eventone.model.dto.BookingDto accept(Long consumerUserId, Long requestId, Long proposalId);
    void reject(Long consumerUserId, Long requestId, Long proposalId);
}
