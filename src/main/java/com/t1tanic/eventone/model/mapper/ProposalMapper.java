package com.t1tanic.eventone.model.mapper;

import com.t1tanic.eventone.model.*;
import com.t1tanic.eventone.model.dto.ProposalDto;
import com.t1tanic.eventone.model.dto.request.proposal.CreateProposalReq;
import org.springframework.stereotype.Component;

@Component
public class ProposalMapper {

    public Proposal from(CreateProposalReq req, EventRequest request,
                         ProviderProfile provider, Offering offering) {
        var p = new Proposal();
        p.setRequest(request);
        p.setProvider(provider);
        p.setOffering(offering);
        p.setPriceCents(req.priceCents());
        p.setCurrency(req.currency() != null ? req.currency() : "EUR");
        p.setMessage(req.message());
        return p;
    }

    public ProposalDto toDto(Proposal p) {
        return new ProposalDto(
                p.getId(),
                p.getRequest().getId(),
                p.getProvider().getId(),
                p.getOffering() != null ? p.getOffering().getId() : null,
                p.getPriceCents(),
                p.getCurrency(),
                p.getMessage(),
                p.getStatus(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }
}
