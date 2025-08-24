package com.t1tanic.eventone.repository.spec;

import com.t1tanic.eventone.model.*;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.Objects;

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
    public static Specification<Offering> availableBetween(LocalDateTime from, LocalDateTime to) {
        return (root, query, cb) -> {
            if (from == null || to == null) return null;
            var slot = Objects.requireNonNull(query).from(AvailabilitySlot.class); // extra root
            query.distinct(true);
            return cb.and(
                    cb.equal(slot.get("provider"), root.get("provider")),
                    cb.lessThanOrEqualTo(slot.get("startsAt"), to),
                    cb.greaterThanOrEqualTo(slot.get("endsAt"), from)
            );
        };
    }

    public static Specification<Offering> activeOnly(){
        return (root, q, cb) -> cb.isTrue(root.get("active"));
    }
}
