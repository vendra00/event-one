package com.t1tanic.eventone.repository;

import com.t1tanic.eventone.model.Cuisine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface CuisineRepository extends JpaRepository<Cuisine, Long> {
    Optional<Cuisine> findByCode(String code);
    boolean existsByCode(String code);
    List<Cuisine> findByCodeIn(Collection<String> codes);
    List<Cuisine> findAllByActiveTrueOrderByNameAsc();
    List<Cuisine> findByNameContainingIgnoreCaseAndActiveTrue(String q);
}
