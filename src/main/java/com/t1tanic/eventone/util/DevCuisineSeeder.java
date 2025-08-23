package com.t1tanic.eventone.util;

import com.t1tanic.eventone.model.Cuisine;
import com.t1tanic.eventone.repository.CuisineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DevCuisineSeeder {

    private final CuisineRepository cuisines;

    /**
     * Idempotent seed for a base cuisine catalog.
     * You can extend this list anytime—existing rows won’t be duplicated.
     */
    @Transactional
    public void seed() {
        Map<String, String> base = new LinkedHashMap<>();
        // Core examples used by your current seeders:
        base.put("italian", "Italian");
        base.put("pasta", "Pasta");          // kept because your current seed uses it
        base.put("catalan", "Catalan");
        base.put("seafood", "Seafood");
        base.put("tapas", "Tapas");

        // A small broader catalog to make filtering useful from day one:
        base.put("spanish", "Spanish");
        base.put("mediterranean", "Mediterranean");
        base.put("vegan", "Vegan");
        base.put("vegetarian", "Vegetarian");
        base.put("mexican", "Mexican");
        base.put("japanese", "Japanese");
        base.put("chinese", "Chinese");
        base.put("indian", "Indian");
        base.put("thai", "Thai");
        base.put("american", "American");
        base.put("bbq", "BBQ");
        base.put("bakery", "Bakery & Pastry");

        int created = 0;
        for (var e : base.entrySet()) {
            String code = e.getKey();
            String name = e.getValue();
            if (!cuisines.existsByCode(code)) {
                var c = new Cuisine();
                c.setCode(code);
                c.setName(name);
                c.setActive(true);
                cuisines.save(c);
                created++;
            }
        }
        log.info("Cuisine seed complete: {} created, {} total", created, cuisines.count());
    }
}
