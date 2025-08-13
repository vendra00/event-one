// OfferingsService.java
package com.t1tanic.eventone.service;

import com.t1tanic.eventone.model.dto.OfferingDto;
import com.t1tanic.eventone.model.dto.request.CreateOfferingReq;
import com.t1tanic.eventone.model.dto.request.UpdateOfferingReq;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OfferingsService {
    Page<OfferingDto> listMine(Long userId, Pageable pageable);
    OfferingDto create(Long userId, CreateOfferingReq req);
    OfferingDto getMine(Long userId, Long offeringId);
    OfferingDto update(Long userId, Long offeringId, UpdateOfferingReq req);
    void delete(Long userId, Long offeringId);
}
