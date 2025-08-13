package com.t1tanic.eventone.model.mapper;


import com.t1tanic.eventone.model.MenuItem;
import com.t1tanic.eventone.model.Offering;
import com.t1tanic.eventone.model.ProviderProfile;
import com.t1tanic.eventone.model.dto.MenuItemDto;
import com.t1tanic.eventone.model.dto.OfferingDto;
import com.t1tanic.eventone.model.dto.request.CreateOfferingReq;
import com.t1tanic.eventone.model.dto.request.UpdateOfferingReq;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class OfferingMapper {

    public Offering from(CreateOfferingReq req, ProviderProfile provider) {
        var o = new Offering();
        o.setProvider(provider);
        o.setTitle(req.title());
        o.setDescription(req.description());
        o.setBasePriceCents(req.basePriceCents());
        o.setCurrency(req.currency());
        o.setMinGuests(req.minGuests());
        o.setMaxGuests(req.maxGuests());
        o.setCuisines(req.cuisines());
        o.setServices(req.services());
        o.setCity(req.city());
        o.setRegion(req.region());

        if (req.menu() != null) {
            var items = new ArrayList<MenuItem>();
            for (var m : req.menu()) {
                var mi = new MenuItem();
                mi.setOffering(o);
                mi.setName(m.name());
                mi.setDescription(m.description());
                mi.setCourse(m.course());
                items.add(mi);
            }
            o.setMenu(items);
        }
        return o;
    }

    public void apply(UpdateOfferingReq req, Offering o) {
        if (req.title() != null) o.setTitle(req.title());
        if (req.description() != null) o.setDescription(req.description());
        if (req.basePriceCents() != null) o.setBasePriceCents(req.basePriceCents());
        if (req.currency() != null) o.setCurrency(req.currency());
        if (req.minGuests() != null) o.setMinGuests(req.minGuests());
        if (req.maxGuests() != null) o.setMaxGuests(req.maxGuests());
        if (req.cuisines() != null) o.setCuisines(req.cuisines());
        if (req.services() != null) o.setServices(req.services());
        if (req.city() != null) o.setCity(req.city());
        if (req.region() != null) o.setRegion(req.region());
        if (req.active() != null) o.setActive(req.active());

        // replace menu if provided
        if (req.menu() != null) {
            o.getMenu().clear();
            for (var m : req.menu()) {
                var mi = new MenuItem();
                mi.setOffering(o);
                mi.setName(m.name());
                mi.setDescription(m.description());
                mi.setCourse(m.course());
                o.getMenu().add(mi);
            }
        }
    }

    public OfferingDto toDto(Offering o) {
        var menu = o.getMenu().stream()
                .map(mi -> new MenuItemDto(mi.getId(), mi.getName(), mi.getDescription(), mi.getCourse()))
                .toList();
        return new OfferingDto(
                o.getId(),
                o.getProvider().getId(),
                o.getTitle(),
                o.getDescription(),
                o.getBasePriceCents(),
                o.getCurrency(),
                o.getMinGuests(),
                o.getMaxGuests(),
                o.getCuisines(),
                o.getServices(),
                o.getCity(),
                o.getRegion(),
                o.isActive(),
                menu
        );
    }
}
