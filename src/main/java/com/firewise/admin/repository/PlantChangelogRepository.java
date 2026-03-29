package com.firewise.admin.repository;

import com.firewise.admin.model.PlantChangelog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlantChangelogRepository extends JpaRepository<PlantChangelog, String> {

    List<PlantChangelog> findByPlantIdOrderByCreatedAtDesc(String plantId);

    Page<PlantChangelog> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
