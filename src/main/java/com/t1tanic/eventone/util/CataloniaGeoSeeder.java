package com.t1tanic.eventone.util;

import com.t1tanic.eventone.model.geo.*;
import com.t1tanic.eventone.repository.geo.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

@Slf4j
@Component
@Profile("dev")
@Order(1) // ensure geo runs before DevDataSeeder
@RequiredArgsConstructor
public class CataloniaGeoSeeder implements ApplicationRunner {

    private final GeoCountryRepository countries;
    private final GeoCommunityRepository communities;
    private final GeoProvinceRepository provinces;
    private final GeoMunicipalityRepository municipalities;
    private final GeoPostalCodeRepository postals;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        // --- Country / Community / Provinces ---
        var es = ensureCountry("ES", "España");
        var ct = ensureCommunity("ES-CT", "Cataluña", es);

        var provB  = ensureProvince("ES-B",  "Barcelona", ct);
        var provGI = ensureProvince("ES-GI", "Girona",    ct);
        var provL  = ensureProvince("ES-L",  "Lleida",    ct);
        var provT  = ensureProvince("ES-T",  "Tarragona", ct);

        Map<String, GeoProvince> provByCode = Map.of(
                "08", provB, "17", provGI, "25", provL, "43", provT
        );

        // ---------- MUNICIPALITIES ----------
        var muniProcessed = new AtomicInteger();
        var muniCreated   = new AtomicInteger();

        try (var in = getResource("/geo/catalonia/municipalities_cat.csv");
             var reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {

            String header = reader.readLine();
            if (header == null) {
                log.warn("municipalities_cat.csv is empty");
            } else {
                header = stripBOM(header);
                char delim = detectDelimiter(header);
                String[] h = split(header, delim);
                Map<String,Integer> idx = indexHeaders(h);

                // Accept both headered files (Codi/Nom) and headerless ones (first row is data)
                Integer codeIdx = first(idx, "codi", "codigo", "ine", "codi_ine", "municipi_ine");
                Integer nameIdx = first(idx, "nom", "name", "nombre", "municipi", "municipio");

                boolean headerLooksLikeData = h.length >= 2 && h[0].trim().matches("\\d{5,6}");
                if (codeIdx == null || nameIdx == null) {
                    if (headerLooksLikeData) {
                        // Treat the 'header' line as the first data row (no header present)
                        processMunicipalityRow(h, 0, 1, provByCode, muniProcessed, muniCreated);
                        // Now parse the rest with fixed positions (0,1)
                        reader.lines().forEach(line -> {
                            if (line.isBlank()) return;
                            String[] parts = split(line, delim);
                            processMunicipalityRow(parts, 0, 1, provByCode, muniProcessed, muniCreated);
                        });
                    } else {
                        throw new IllegalStateException("municipalities_cat.csv: couldn't find code/name columns. First line=" + header);
                    }
                } else {
                    // Proper header present
                    reader.lines().forEach(line -> {
                        if (line.isBlank()) return;
                        String[] parts = split(line, delim);
                        if (parts.length <= Math.max(codeIdx, nameIdx)) return;
                        processMunicipalityRow(parts, codeIdx, nameIdx, provByCode, muniProcessed, muniCreated);
                    });
                }
            }
        }

        // ---------- POSTAL CODES ----------
        var cpProcessed    = new AtomicInteger();
        var cpMuniMatch    = new AtomicInteger();
        var cpActuallyMade = new AtomicInteger();

        try (var in = getResource("/geo/catalonia/postal_codes_cat.csv");
             var reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {

            String header = reader.readLine();
            if (header == null) {
                log.warn("postal_codes_cat.csv is empty");
            } else {
                header = stripBOM(header);
                char delim = detectDelimiter(header);
                String[] h = split(header, delim);
                Map<String,Integer> idx = indexHeaders(h);

                Integer cpIdx = first(idx, "codi_postal", "codigo_postal", "postal_code", "cp", "cod_postal");
                Integer cmIdx = first(idx, "codi_municipi", "codigo_municipio", "ine", "municipi_ine");
                if (cpIdx == null || cmIdx == null) {
                    throw new IllegalStateException("postal_codes_cat.csv: couldn't find 'Codi postal'/'Codi municipi'. First line=" + header);
                }

                reader.lines().forEach(line -> {
                    if (line.isBlank()) return;
                    cpProcessed.incrementAndGet();
                    String[] parts = split(line, delim);
                    if (parts.length <= Math.max(cpIdx, cmIdx)) return;

                    String cp  = unquote(parts[cpIdx]);
                    String cm6 = unquote(parts[cmIdx]);

                    // Convert 6-digit municipal code -> first 5 digits (drop check digit)
                    String ine = cm6.matches("\\d{6}") ? cm6.substring(0, 5) : cm6;

                    if (!ine.matches("\\d{5}") || !cp.matches("\\d{5}")) return;

                    municipalities.findById(ine).ifPresent(m -> {
                        cpMuniMatch.incrementAndGet();
                        if (!postals.existsById(cp)) {
                            ensurePostal(cp, m);
                            cpActuallyMade.incrementAndGet();
                        }
                    });
                });
            }
        }

        log.info(
                "Catalonia geo seed done. municipalities: processed={}, created={}; postals: processed={}, muni_match={}, created={}",
                muniProcessed.get(), muniCreated.get(), cpProcessed.get(), cpMuniMatch.get(), cpActuallyMade.get()
        );
    }

    // ---------- row processor for municipalities ----------
    private void processMunicipalityRow(
            String[] parts, int codeIdx, int nameIdx, Map<String, GeoProvince> provByCode,
            AtomicInteger processed, AtomicInteger created
    ) {
        processed.incrementAndGet();

        if (parts.length <= Math.max(codeIdx, nameIdx)) return;
        String codeRaw = unquote(parts[codeIdx]);
        String name    = unquote(parts[nameIdx]);

        // Accept 6-digit "Codi" -> 5-digit INE by dropping last digit
        String ine = codeRaw.matches("\\d{6}") ? codeRaw.substring(0, 5) : codeRaw;
        if (!ine.matches("\\d{5}") || name.isEmpty()) return;

        var prov = provByCode.get(ine.substring(0, 2));
        if (prov == null) return;

        boolean existed = municipalities.existsById(ine);
        ensureMunicipality(ine, name, prov);
        if (!existed) created.incrementAndGet();
    }

    // ---------- helpers ----------
    private static String stripBOM(String s) {
        return s != null && !s.isEmpty() && s.charAt(0) == '\uFEFF' ? s.substring(1) : s;
    }

    private static String unquote(String s) {
        s = s.trim();
        if (s.startsWith("\"") && s.endsWith("\"") && s.length() >= 2) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }

    private java.io.InputStream getResource(String path) {
        var in = getClass().getResourceAsStream(path);
        if (in == null) throw new IllegalStateException("Resource not found on classpath: " + path);
        return in;
    }

    // delimiter detection across ; , tab
    private static char detectDelimiter(String header) {
        int sc = count(header, ';'), cc = count(header, ','), tc = count(header, '\t');
        if (tc >= sc && tc >= cc) return '\t';
        if (sc >= cc) return ';';
        return ',';
    }
    private static int count(String s, char c) { int n=0; for (int i=0;i<s.length();i++) if (s.charAt(i)==c) n++; return n; }

    private static String[] split(String line, char delim) {
        String[] out = line.split(Pattern.quote(String.valueOf(delim)), -1);
        for (int i=0;i<out.length;i++) out[i] = out[i].trim();
        return out;
    }

    private static Map<String,Integer> indexHeaders(String[] headers) {
        Map<String,Integer> idx = new HashMap<>();
        for (int i=0;i<headers.length;i++) idx.put(norm(headers[i]), i);
        return idx;
    }

    private static String norm(String s) {
        String t = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}","");
        return t.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+","_").replaceAll("_+$","");
    }

    @SafeVarargs
    private static Integer first(Map<String,Integer> map, String... keys) {
        for (String k : keys) {
            Integer i = map.get(k);
            if (i != null) return i;
        }
        return null;
    }

    private GeoCountry ensureCountry(String code, String name) {
        return countries.findById(code).orElseGet(() -> {
            var x = new GeoCountry();
            x.setCode(code); x.setName(name);
            return countries.save(x);
        });
    }
    private GeoCommunity ensureCommunity(String code, String name, GeoCountry country) {
        return communities.findById(code).orElseGet(() -> {
            var x = new GeoCommunity();
            x.setCode(code); x.setName(name); x.setCountry(country);
            return communities.save(x);
        });
    }
    private GeoProvince ensureProvince(String code, String name, GeoCommunity community) {
        return provinces.findById(code).orElseGet(() -> {
            var x = new GeoProvince();
            x.setCode(code); x.setName(name); x.setCommunity(community);
            return provinces.save(x);
        });
    }
    private GeoMunicipality ensureMunicipality(String code, String name, GeoProvince province) {
        return municipalities.findById(code).orElseGet(() -> {
            var x = new GeoMunicipality();
            x.setCode(code); x.setName(name); x.setProvince(province);
            return municipalities.save(x);
        });
    }
    private GeoPostalCode ensurePostal(String code, GeoMunicipality municipality) {
        return postals.findById(code).orElseGet(() -> {
            var x = new GeoPostalCode();
            x.setCode(code); x.setMunicipality(municipality);
            return postals.save(x);
        });
    }
}
