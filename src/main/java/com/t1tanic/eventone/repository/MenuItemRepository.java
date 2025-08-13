package com.t1tanic.eventone.repository;

import com.t1tanic.eventone.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByOfferingId(Long offeringId);
}
