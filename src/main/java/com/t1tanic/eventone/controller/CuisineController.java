package com.t1tanic.eventone.controller;

import com.t1tanic.eventone.model.Cuisine;
import com.t1tanic.eventone.model.dto.CuisineDto;
import com.t1tanic.eventone.repository.CuisineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cuisines")
@RequiredArgsConstructor
public class CuisineController {

    private static final int DEFAULT_LIMIT = 200;

    private final CuisineRepository cuisines;

    /** List active cuisines (optionally filter by name) */
    @PreAuthorize("hasAnyRole('PROVIDER','CONSUMER','ADMIN')")
    @GetMapping
    public List<CuisineDto> list(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "limit", required = false) Integer limit
    ) {
        int cap = (limit != null && limit > 0) ? Math.min(limit, 500) : DEFAULT_LIMIT;

        // Prefer repo helpers if present; otherwise filter in memory
        List<Cuisine> base;
        if (StringUtils.hasText(q)) {
            String needle = q.trim();
            try {
                // requires: List<Cuisine> findByNameContainingIgnoreCaseAndActiveTrue(String q);
                base = cuisines.findByNameContainingIgnoreCaseAndActiveTrue(needle);
            } catch (Throwable ignored) {
                base = cuisines.findAll().stream()
                        .filter(Cuisine::isActive)
                        .filter(c -> c.getName() != null && c.getName().toLowerCase().contains(needle.toLowerCase()))
                        .toList();
            }
        } else {
            try {
                // requires: List<Cuisine> findAllByActiveTrueOrderByNameAsc();
                base = cuisines.findAllByActiveTrueOrderByNameAsc();
            } catch (Throwable ignored) {
                base = cuisines.findAll().stream().filter(Cuisine::isActive).toList();
            }
        }

        return base.stream()
                .sorted(Comparator.comparing(Cuisine::getName, Comparator.nullsLast(String::compareToIgnoreCase)))
                .limit(cap)
                .map(c -> new CuisineDto(c.getCode(), c.getName()))
                .toList();
    }

    /** Fetch a specific set of cuisines by code (e.g., /api/cuisines/by-codes?code=italian&code=tapas) */
    @PreAuthorize("hasAnyRole('PROVIDER','CONSUMER','ADMIN')")
    @GetMapping("/by-codes")
    public List<CuisineDto> byCodes(@RequestParam("code") List<String> codes) {
        if (codes == null || codes.isEmpty()) return List.of();
        var norm = codes.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toLowerCase)
                .distinct()
                .collect(Collectors.toList());

        // requires: List<Cuisine> findByCodeIn(Collection<String> codes);
        var found = cuisines.findByCodeIn(norm);
        return found.stream()
                .sorted(Comparator.comparing(Cuisine::getName, Comparator.nullsLast(String::compareToIgnoreCase)))
                .map(c -> new CuisineDto(c.getCode(), c.getName()))
                .toList();
    }
}
