package com.firewise.admin.repository;

import com.firewise.admin.model.Plant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlantRepository extends JpaRepository<Plant, String> {

    @Query("SELECT p FROM Plant p WHERE " +
           "LOWER(p.genus) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.species) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.commonName) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Plant> search(@Param("search") String search, Pageable pageable);
}
