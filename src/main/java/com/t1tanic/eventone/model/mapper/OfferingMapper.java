package com.t1tanic.eventone.model.mapper;

import com.t1tanic.eventone.model.Cuisine;
import com.t1tanic.eventone.model.MenuItem;
import com.t1tanic.eventone.model.Offering;
import com.t1tanic.eventone.model.ProviderProfile;
import com.t1tanic.eventone.model.dto.CuisineDto;
import com.t1tanic.eventone.model.dto.MenuItemDto;
import com.t1tanic.eventone.model.dto.OfferingDto;
import com.t1tanic.eventone.model.dto.request.offering.CreateOfferingReq;
import com.t1tanic.eventone.model.dto.request.offering.UpdateOfferingReq;
import com.t1tanic.eventone.repository.CuisineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OfferingMapper {

    private final CuisineRepository cuisineRepo;

    public Offering from(CreateOfferingReq req, ProviderProfile provider) {
        var o = new Offering();
        o.setProvider(provider);
        o.setTitle(req.title());
        o.setDescription(req.description());
        o.setBasePriceCents(req.basePriceCents());
        o.setCurrency(req.currency());
        o.setMinGuests(req.minGuests());
        o.setMaxGuests(req.maxGuests());

        // Offering still stores CSV for now
        o.setCuisines(req.cuisines());  // keep as-is until Offering is migrated to M2M
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
        if (req.cuisines() != null) o.setCuisines(req.cuisines()); // CSV
        if (req.services() != null) o.setServices(req.services());
        if (req.city() != null) o.setCity(req.city());
        if (req.region() != null) o.setRegion(req.region());
        if (req.active() != null) o.setActive(req.active());

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

        var cuisineDtos = toCuisineDtos(o.getCuisines());

        return new OfferingDto(
                o.getId(),
                o.getProvider().getId(),
                o.getTitle(),
                o.getDescription(),
                o.getBasePriceCents(),
                o.getCurrency(),
                o.getMinGuests(),
                o.getMaxGuests(),
                cuisineDtos,
                o.getServices(),
                o.getCity(),
                o.getRegion(),
                o.isActive(),
                menu
        );
    }

    // ---------- helpers ----------

    private List<CuisineDto> toCuisineDtos(String csv) {
        var codes = parseCsv(csv);
        if (codes.isEmpty()) return List.of();

        return cuisineRepo.findByCodeIn(codes).stream()
                .sorted(Comparator.comparing(Cuisine::getName))
                .map(c -> new CuisineDto(c.getCode(), c.getName()))
                .toList();
    }

    private static List<String> parseCsv(String csv) {
        if (csv == null || csv.isBlank()) return List.of();
        return Arrays.stream(csv.split("[,;]"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toLowerCase)
                .distinct()
                .limit(20)
                .collect(Collectors.toList());
    }
}
