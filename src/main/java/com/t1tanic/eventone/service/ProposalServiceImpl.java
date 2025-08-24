package com.t1tanic.eventone.service;

import com.t1tanic.eventone.model.*;
import com.t1tanic.eventone.model.dto.ProposalDto;
import com.t1tanic.eventone.model.dto.request.proposal.CreateProposalReq;
import com.t1tanic.eventone.model.enums.BookingStatus;
import com.t1tanic.eventone.model.enums.EventRequestStatus;
import com.t1tanic.eventone.model.enums.ProposalStatus;
import com.t1tanic.eventone.model.mapper.BookingMapper;
import com.t1tanic.eventone.model.mapper.ProposalMapper;
import com.t1tanic.eventone.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProposalServiceImpl implements ProposalService {

    private final ProposalRepository proposals;
    private final EventRequestRepository requests;
    private final ProviderProfileRepository providers;
    private final OfferingRepository offerings;
    private final BookingRepository bookings;

    private final ProposalMapper mapper;
    private final BookingMapper bookingMapper;

    @Override
    public ProposalDto create(Long providerUserId, Long requestId, CreateProposalReq req) {
        var provider = providers.findByUserId(providerUserId)
                .orElseThrow(() -> new EntityNotFoundException("provider_profile_not_found_for_user:" + providerUserId));

        var er = requests.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("request_not_found:" + requestId));

        // (Optional) if request is targeted to a different provider, block
        if ( er.getProvider() != null && !er.getProvider().getId().equals(provider.getId()) ) {
            throw new IllegalStateException("request_targeted_to_other_provider");
        }
        if (er.getStatus() != EventRequestStatus.OPEN) {
            throw new IllegalStateException("request_not_open");
        }

        Offering off = null;
        if (req.offeringId() != null) {
            off = offerings.findById(req.offeringId())
                    .orElseThrow(() -> new EntityNotFoundException("offering_not_found:" + req.offeringId()));
            if (!off.getProvider().getId().equals(provider.getId())) {
                throw new IllegalArgumentException("offering_not_owned_by_provider");
            }
        }

        var entity = mapper.from(req, er, provider, off);
        var saved = proposals.save(entity);
        log.info("Proposal id={} created for requestId={} by providerId={}", saved.getId(), er.getId(), provider.getId());
        return mapper.toDto(saved);
    }

    @Override @Transactional(readOnly = true)
    public Page<ProposalDto> listForProvider(Long providerUserId, Pageable pageable) {
        var provider = providers.findByUserId(providerUserId)
                .orElseThrow(() -> new EntityNotFoundException("provider_profile_not_found_for_user:" + providerUserId));
        return proposals.findByProviderId(provider.getId(), pageable).map(mapper::toDto);
    }

    @Override @Transactional(readOnly = true)
    public Page<ProposalDto> listForRequestAsConsumer(Long consumerUserId, Long requestId, Pageable pageable) {
        var er = requests.findById(requestId)
                .filter(r -> r.getConsumer().getId().equals(consumerUserId))
                .orElseThrow(() -> new EntityNotFoundException("request_not_found_or_not_owned:" + requestId));
        return proposals.findByRequestId(er.getId(), pageable).map(mapper::toDto);
    }

    @Override @Transactional(readOnly = true)
    public ProposalDto getForProvider(Long providerUserId, Long proposalId) {
        var provider = providers.findByUserId(providerUserId)
                .orElseThrow(() -> new EntityNotFoundException("provider_profile_not_found_for_user:" + providerUserId));
        var p = proposals.findById(proposalId)
                .filter(x -> x.getProvider().getId().equals(provider.getId()))
                .orElseThrow(() -> new EntityNotFoundException("proposal_not_found_or_not_owned:" + proposalId));
        return mapper.toDto(p);
    }

    @Override @Transactional(readOnly = true)
    public ProposalDto getForConsumer(Long consumerUserId, Long proposalId) {
        var p = proposals.findById(proposalId)
                .filter(x -> x.getRequest().getConsumer().getId().equals(consumerUserId))
                .orElseThrow(() -> new EntityNotFoundException("proposal_not_found_or_not_owned:" + proposalId));
        return mapper.toDto(p);
    }

    @Override
    public void withdraw(Long providerUserId, Long proposalId) {
        var provider = providers.findByUserId(providerUserId)
                .orElseThrow(() -> new EntityNotFoundException("provider_profile_not_found_for_user:" + providerUserId));
        var p = proposals.findById(proposalId)
                .filter(x -> x.getProvider().getId().equals(provider.getId()))
                .orElseThrow(() -> new EntityNotFoundException("proposal_not_found_or_not_owned:" + proposalId));
        if (p.getStatus() != com.t1tanic.eventone.model.enums.ProposalStatus.SENT) {
            throw new IllegalStateException("only_sent_can_be_withdrawn");
        }
        p.setStatus(com.t1tanic.eventone.model.enums.ProposalStatus.WITHDRAWN);
        proposals.save(p);
    }

    @Override
    public com.t1tanic.eventone.model.dto.BookingDto accept(Long consumerUserId, Long requestId, Long proposalId) {
        var er = requests.findById(requestId)
                .filter(r -> r.getConsumer().getId().equals(consumerUserId))
                .orElseThrow(() -> new EntityNotFoundException("request_not_found_or_not_owned:" + requestId));
        if (er.getStatus() != EventRequestStatus.OPEN) {
            throw new IllegalStateException("request_not_open");
        }

        var p = proposals.findById(proposalId)
                .filter(x -> x.getRequest().getId().equals(er.getId()))
                .orElseThrow(() -> new EntityNotFoundException("proposal_not_found_for_request:" + proposalId));
        if (p.getStatus() != ProposalStatus.SENT) {
            throw new IllegalStateException("proposal_not_in_sent_state");
        }

        // accept proposal
        p.setStatus(ProposalStatus.ACCEPTED);
        proposals.save(p);

        // reject others for this request
        for (var other : proposals.findByRequestId(er.getId())) {
            if (!other.getId().equals(p.getId()) && other.getStatus() == ProposalStatus.SENT) {
                other.setStatus(ProposalStatus.REJECTED);
                proposals.save(other);
            }
        }

        // mark request as BOOKED (add value to your EventRequestStatus enum if not present)
        if (er.getStatus() == EventRequestStatus.OPEN) {
            try {
                er.setStatus(EventRequestStatus.valueOf("BOOKED"));
            } catch (IllegalArgumentException ignore) {
                // if you haven't added BOOKED yet, keep OPEN
            }
        }

        // create booking (event date = request.startsAt, headcount = request.guests)
        var b = new Booking();
        b.setProposal(p);
        b.setEventDate(er.getStartsAt());
        b.setHeadcount(er.getGuests());
        b.setStatus(BookingStatus.PENDING);
        var saved = bookings.save(b);

        log.info("Booking id={} created from proposalId={} requestId={}", saved.getId(), p.getId(), er.getId());
        return bookingMapper.toDto(saved);
    }

    @Override
    public void reject(Long consumerUserId, Long requestId, Long proposalId) {
        var er = requests.findById(requestId)
                .filter(r -> r.getConsumer().getId().equals(consumerUserId))
                .orElseThrow(() -> new EntityNotFoundException("request_not_found_or_not_owned:" + requestId));
        var p = proposals.findById(proposalId)
                .filter(x -> x.getRequest().getId().equals(er.getId()))
                .orElseThrow(() -> new EntityNotFoundException("proposal_not_found_for_request:" + proposalId));
        if (p.getStatus() != ProposalStatus.SENT) throw new IllegalStateException("proposal_not_in_sent_state");
        p.setStatus(ProposalStatus.REJECTED);
        proposals.save(p);
    }
}
