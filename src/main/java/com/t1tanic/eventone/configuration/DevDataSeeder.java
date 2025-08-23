package com.t1tanic.eventone.configuration;

import com.t1tanic.eventone.model.*;
import com.t1tanic.eventone.model.enums.*;
import com.t1tanic.eventone.model.geo.*;
import com.t1tanic.eventone.repository.*;
import com.t1tanic.eventone.repository.geo.*;
import com.t1tanic.eventone.util.DevCuisineSeeder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

@Slf4j
@Component
@Profile("dev") // run with spring.profiles.active=dev
@RequiredArgsConstructor
public class DevDataSeeder implements ApplicationRunner {

    // Repositories for all entities involved in the seed
    private final AppUserRepository users;
    private final ProviderProfileRepository providers;
    private final OfferingRepository offerings;
    private final AvailabilitySlotRepository slots;
    private final EventRequestRepository requests;
    private final ProposalRepository proposals;
    private final BookingRepository bookings;
    private final CuisineRepository cuisineRepo;

    // Password encoder to hash user passwords
    private final PasswordEncoder encoder;

    // Geo repos for normalized locations
    private final GeoCountryRepository countries;
    private final GeoCommunityRepository communities;
    private final GeoProvinceRepository provinces;
    private final GeoMunicipalityRepository municipalities;
    private final GeoPostalCodeRepository postals;

    private final DevCuisineSeeder cuisineSeeder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {

        // Seed cuisines first (idempotent)
        cuisineSeeder.seed();

        if (users.count() > 0) {
            log.info("Seed skipped: users already present.");
            return;
        }

        // --- Ensure minimal geo data (Spain: Madrid + Cataluña/Barcelona) ---
        var es = ensureCountry("ES", "España");

        // Madrid
        var md = ensureCommunity("ES-MD", "Comunidad de Madrid", es);
        var provM = ensureProvince("ES-M", "Madrid", md);
        var muniMadrid = ensureMunicipality("28079", "Madrid", provM);
        // seed a couple of postals commonly used in center/east areas
        ensurePostal("28001", muniMadrid); // (Salamanca) - fine for dev
        ensurePostal("28007", muniMadrid); // (Retiro)
        var madridCentro28001 = geo(es, md, provM, muniMadrid, "Centro", "28001");

        // Cataluña / Barcelona
        var ct = ensureCommunity("ES-CT", "Cataluña", es);
        var provB = ensureProvince("ES-B", "Barcelona", ct);
        var muniBarcelona = ensureMunicipality("08019", "Barcelona", provB);
        ensurePostal("08002", muniBarcelona); // Barri Gòtic area
        var barcelonaGotic08002 = geo(es, ct, provB, muniBarcelona, "Gòtic", "08002");

        // --- Users ---
        var consumer = new AppUser();
        consumer.setEmail("consumer1@example.com");
        consumer.setPasswordHash(encoder.encode("Secret123!"));
        consumer.setRoles(EnumSet.of(UserRole.CONSUMER));
        users.save(consumer);

        var providerUser = new AppUser();
        providerUser.setEmail("provider1@example.com");
        providerUser.setPasswordHash(encoder.encode("Secret123!"));
        providerUser.setRoles(EnumSet.of(UserRole.PROVIDER));
        users.save(providerUser);

        // Second provider (Barcelona)
        var providerUser2 = new AppUser();
        providerUser2.setEmail("provider2@example.com");
        providerUser2.setPasswordHash(encoder.encode("Secret123!"));
        providerUser2.setRoles(EnumSet.of(UserRole.PROVIDER));
        users.save(providerUser2);

        log.info("Seed: users created consumerId={} providerUserId1={} providerUserId2={}",
                consumer.getId(), providerUser.getId(), providerUser2.getId());

        // --- Provider profile (Madrid) ---
        var provider = new ProviderProfile();
        provider.setUser(providerUser);
        provider.setDisplayName("Chef Mario");
        provider.setKind(ProviderKind.INDIVIDUAL);
        provider.setBio("Italian private chef");
        provider.setCuisines(cuisines("italian","pasta"));
        provider.setLocation(madridCentro28001);
        provider.setMinGuests(4);
        provider.setMaxGuests(20);
        provider.setServices("waiters");
        providers.save(provider);

        // --- Provider profile (Barcelona) ---
        var providerBcn = new ProviderProfile();
        providerBcn.setUser(providerUser2);
        providerBcn.setDisplayName("Chef Jordi");
        providerBcn.setKind(ProviderKind.INDIVIDUAL);
        providerBcn.setBio("Catalan & Mediterranean cuisine");
        providerBcn.setLocation(barcelonaGotic08002);
        providerBcn.setMinGuests(2);
        providerBcn.setMaxGuests(16);
        providerBcn.setServices("waiters,bar");
        providerBcn.setCuisines(cuisines("catalan","seafood","tapas","mediterranean"));
        providers.save(providerBcn);

        log.info("Seed: provider profiles created ids={}, {}", provider.getId(), providerBcn.getId());

        // --- Offering (Madrid; legacy city/region kept for now) ---
        var off = new Offering();
        off.setProvider(provider);
        off.setTitle("Italian Family Dinner");
        off.setDescription("4 courses, 2 waiters optional");
        off.setBasePriceCents(35_000);
        off.setCurrency("EUR");
        off.setMinGuests(4);
        off.setMaxGuests(20);
        off.setCuisines("italian,pasta");
        off.setServices("waiters");
        off.setCity("Madrid");
        off.setRegion("Madrid");
        off.setActive(true);
        offerings.save(off);

        // --- Offering (Barcelona; legacy city/region kept for now) ---
        var offB = new Offering();
        offB.setProvider(providerBcn);
        offB.setTitle("Catalan Tasting Menu");
        offB.setDescription("Seasonal Catalan dishes, seafood & tapas");
        offB.setBasePriceCents(38_000);
        offB.setCurrency("EUR");
        offB.setMinGuests(2);
        offB.setMaxGuests(16);
        offB.setCuisines("catalan,seafood,tapas");
        offB.setServices("waiters,bar");
        offB.setCity("Barcelona");
        offB.setRegion("Barcelona");
        offB.setActive(true);
        offerings.save(offB);

        log.info("Seed: offerings created ids={}, {}", off.getId(), offB.getId());

        // --- Availability (Madrid) ---
        var s1 = new AvailabilitySlot();
        s1.setProvider(provider);
        s1.setStartsAt(LocalDateTime.of(2025, 9, 5, 17, 0));
        s1.setEndsAt(LocalDateTime.of(2025, 9, 5, 23, 0));
        s1.setNote("Evening block");
        slots.save(s1);

        var s2 = new AvailabilitySlot();
        s2.setProvider(provider);
        s2.setStartsAt(LocalDateTime.of(2025, 9, 10, 16, 0));
        s2.setEndsAt(LocalDateTime.of(2025, 9, 10, 22, 30));
        s2.setNote("Dinner block");
        slots.save(s2);

        // --- Availability (Barcelona) ---
        var sB1 = new AvailabilitySlot();
        sB1.setProvider(providerBcn);
        sB1.setStartsAt(LocalDateTime.of(2025, 9, 6, 18, 0));
        sB1.setEndsAt(LocalDateTime.of(2025, 9, 6, 23, 0));
        sB1.setNote("Weekend tasting");
        slots.save(sB1);

        var sB2 = new AvailabilitySlot();
        sB2.setProvider(providerBcn);
        sB2.setStartsAt(LocalDateTime.of(2025, 9, 11, 17, 30));
        sB2.setEndsAt(LocalDateTime.of(2025, 9, 11, 22, 30));
        sB2.setNote("Tapas night");
        slots.save(sB2);

        log.info("Seed: availability slots created");

        // --- Event requests (Madrid; normalized geo) ---
        var r1 = new EventRequest();
        r1.setConsumer(consumer);
        r1.setTitle("Family birthday dinner");
        r1.setStartsAt(LocalDateTime.of(2025, 9, 5, 17, 0));
        r1.setEndsAt(LocalDateTime.of(2025, 9, 5, 21, 0));
        r1.setGuests(12);
        r1.setLocation(geo(es, md, provM, muniMadrid, "Centro", "28001"));
        r1.setCuisines(cuisines("italian","pasta"));
        r1.setServices("waiters");
        r1.setBudgetCents(40_000);
        r1.setCurrency("EUR");
        r1.setNotes("Prefer vegetarian options");
        r1.setStatus(EventRequestStatus.OPEN);
        requests.save(r1);

        var r2 = new EventRequest();
        r2.setConsumer(consumer);
        r2.setTitle("Italian dinner at home");
        r2.setOffering(off);              // targeted to offering/provider
        r2.setProvider(provider);
        r2.setStartsAt(LocalDateTime.of(2025, 9, 10, 16, 30));
        r2.setEndsAt(LocalDateTime.of(2025, 9, 10, 20, 30));
        r2.setGuests(8);
        r2.setLocation(geo(es, md, provM, muniMadrid, "Retiro", "28007"));
        r2.setNotes("Birthday");
        r2.setCurrency("EUR");
        r2.setStatus(EventRequestStatus.OPEN);
        requests.save(r2);

        // --- Event request (Barcelona; normalized geo) ---
        var r4 = new EventRequest();
        r4.setConsumer(consumer);
        r4.setTitle("Seafood dinner with friends");
        r4.setStartsAt(LocalDateTime.of(2025, 9, 11, 19, 0));
        r4.setEndsAt(LocalDateTime.of(2025, 9, 11, 23, 0));
        r4.setGuests(6);
        r4.setLocation(geo(es, ct, provB, muniBarcelona, "Gòtic", "08002"));
        r4.setCuisines(cuisines("seafood","tapas"));
        r4.setServices("waiters");
        r4.setBudgetCents(45_000);
        r4.setCurrency("EUR");
        r4.setNotes("Paella preferred");
        r4.setStatus(EventRequestStatus.OPEN);
        requests.save(r4);

        log.info("Seed: event requests created ids={}, {}, {}", r1.getId(), r2.getId(), r4.getId());

        // --- One proposal for r1 (provider -> consumer) ---
        var p1 = new Proposal();
        p1.setRequest(r1);
        p1.setProvider(provider);
        p1.setOffering(off);
        p1.setPriceCents(42_000);
        p1.setCurrency("EUR");
        p1.setMessage("Includes 3 courses + setup");
        proposals.save(p1);

        log.info("Seed: proposal created id={} for requestId={}", p1.getId(), r1.getId());

        // --- Accept p1 -> create booking (PENDING) ---
        p1.setStatus(ProposalStatus.ACCEPTED);
        proposals.save(p1);

        var b1 = new Booking();
        b1.setProposal(p1);
        b1.setEventDate(r1.getStartsAt());
        b1.setHeadcount(r1.getGuests());
        b1.setStatus(BookingStatus.PENDING);
        bookings.save(b1);

        log.info("Seed: booking created id={} from proposalId={}", b1.getId(), p1.getId());

        log.info("Seed complete. Log in with:");
        log.info("  CONSUMER  email=consumer1@example.com  password=Secret123!");
        log.info("  PROVIDER1 email=provider1@example.com  password=Secret123!");
        log.info("  PROVIDER2 email=provider2@example.com  password=Secret123!");
    }

    // ---------- helpers ----------

    private GeoLocation geo(GeoCountry c, GeoCommunity cc, GeoProvince p, GeoMunicipality m, String locality, String postal) {
        var gl = new GeoLocation();
        gl.setCountry(c);
        gl.setCommunity(cc);
        gl.setProvince(p);
        gl.setMunicipality(m);
        gl.setLocality(locality);
        gl.setPostalCode(postal);
        return gl;
    }

    private GeoCountry ensureCountry(String code, String name) {
        return countries.findById(code).orElseGet(() -> {
            var x = new GeoCountry();
            x.setCode(code);
            x.setName(name);
            return countries.save(x);
        });
    }

    private GeoCommunity ensureCommunity(String code, String name, GeoCountry country) {
        return communities.findById(code).orElseGet(() -> {
            var x = new GeoCommunity();
            x.setCode(code);
            x.setName(name);
            x.setCountry(country);
            return communities.save(x);
        });
    }

    private GeoProvince ensureProvince(String code, String name, GeoCommunity community) {
        return provinces.findById(code).orElseGet(() -> {
            var x = new GeoProvince();
            x.setCode(code);
            x.setName(name);
            x.setCommunity(community);
            return provinces.save(x);
        });
    }

    private GeoMunicipality ensureMunicipality(String code, String name, GeoProvince province) {
        return municipalities.findById(code).orElseGet(() -> {
            var x = new GeoMunicipality();
            x.setCode(code);
            x.setName(name);
            x.setProvince(province);
            return municipalities.save(x);
        });
    }

    private GeoPostalCode ensurePostal(String code, GeoMunicipality municipality) {
        return postals.findById(code).orElseGet(() -> {
            var x = new GeoPostalCode();
            x.setCode(code);
            x.setMunicipality(municipality);
            return postals.save(x);
        });
    }

    private java.util.Set<Cuisine> cuisines(String... codes) {
        var list = Arrays.stream(codes)
                .filter(java.util.Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toLowerCase)
                .distinct()
                .toList();

        var found = cuisineRepo.findByCodeIn(list);
        var foundCodes = found.stream().map(c -> c.getCode().toLowerCase()).collect(Collectors.toSet());
        var missing = new LinkedHashSet<>(list);
        missing.removeAll(foundCodes);
        if (!missing.isEmpty()) {
            log.warn("Dev seed: unknown cuisines {}", missing);
        }
        return new java.util.HashSet<>(found);
    }
}
