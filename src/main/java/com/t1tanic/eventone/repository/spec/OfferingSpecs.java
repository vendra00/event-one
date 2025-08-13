package com.t1tanic.eventone.repository.spec;

import com.t1tanic.eventone.model.*;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public final class OfferingSpecs {
    private OfferingSpecs(){}

    public static Specification<Offering> city(String city){
        return (root, q, cb) -> city == null || city.isBlank() ? null :
                cb.equal(cb.lower(root.get("city")), city.toLowerCase());
    }

    public static Specification<Offering> region(String region){
        return (root, q, cb) -> region == null || region.isBlank() ? null :
                cb.equal(cb.lower(root.get("region")), region.toLowerCase());
    }

    public static Specification<Offering> guests(Integer guests){
        return (root, q, cb) -> guests == null ? null :
                cb.and(
                        cb.or(root.get("minGuests").isNull(), cb.le(root.get("minGuests"), guests)),
                        cb.or(root.get("maxGuests").isNull(), cb.ge(root.get("maxGuests"), guests))
                );
    }

    // naive tag contains (MVP). Later: normalize to a join table.
    public static Specification<Offering> hasCuisine(String cuisine){
        return (root, q, cb) -> cuisine == null || cuisine.isBlank() ? null :
                cb.like(cb.lower(root.get("cuisines")), "%" + cuisine.toLowerCase() + "%");
    }

    public static Specification<Offering> hasService(String service){
        return (root, q, cb) -> service == null || service.isBlank() ? null :
                cb.like(cb.lower(root.get("services")), "%" + service.toLowerCase() + "%");
    }

    /** Overlap with [from,to] through provider's availability slots */
    public static Specification<Offering> availableBetween(LocalDateTime from, LocalDateTime to){
        return (root, q, cb) -> {
            if (from == null || to == null) return null;
            // exists (select 1 from availability_slot s
            // where s.provider_id = offering.provider_id and s.starts_at <= :to and s.ends_at >= :from)
            Subquery<Long> sq = q.subquery(Long.class);
            Root<AvailabilitySlot> s = sq.from(AvailabilitySlot.class);
            sq.select(cb.literal(1L))
                    .where(
                            cb.equal(s.get("provider").get("id"), root.get("provider").get("id")),
                            cb.lessThanOrEqualTo(s.get("startsAt"), to),
                            cb.greaterThanOrEqualTo(s.get("endsAt"), from)
                    );
            return cb.exists(sq);
        };
    }

    public static Specification<Offering> activeOnly(){
        return (root, q, cb) -> cb.isTrue(root.get("active"));
    }
}
