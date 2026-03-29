package com.firewise.admin.controller;

import com.firewise.admin.dto.PlantDetailDto;
import com.firewise.admin.dto.PlantSummaryDto;
import com.firewise.admin.model.AttributeValue;
import com.firewise.admin.model.Plant;
import com.firewise.admin.repository.AttributeValueRepository;
import com.firewise.admin.service.PlantService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plants")
public class PlantController {

    private final PlantService plantService;
    private final AttributeValueRepository attributeValueRepository;

    public PlantController(PlantService plantService, AttributeValueRepository attributeValueRepository) {
        this.plantService = plantService;
        this.attributeValueRepository = attributeValueRepository;
    }

    @GetMapping
    public Page<PlantSummaryDto> getPlants(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return plantService.getPlants(search, PageRequest.of(page, size, Sort.by("genus", "species")));
    }

    @GetMapping("/{id}")
    public PlantDetailDto getPlant(@PathVariable String id) {
        return plantService.getPlantDetail(id);
    }

    @PostMapping
    public ResponseEntity<Plant> createPlant(@RequestBody Plant plant) {
        return ResponseEntity.ok(plantService.createPlant(plant));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Plant> updatePlant(@PathVariable String id, @RequestBody Plant plant) {
        return ResponseEntity.ok(plantService.updatePlant(id, plant));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlant(@PathVariable String id) {
        plantService.deletePlant(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/values")
    public List<AttributeValue> getPlantValues(@PathVariable String id) {
        return attributeValueRepository.findByPlantId(id);
    }
}
