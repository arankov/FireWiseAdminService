package com.firewise.admin.service;

import com.firewise.admin.dto.*;
import com.firewise.admin.exception.ResourceNotFoundException;
import com.firewise.admin.model.Attribute;
import com.firewise.admin.model.AttributeValue;
import com.firewise.admin.model.Plant;
import com.firewise.admin.repository.AttributeRepository;
import com.firewise.admin.repository.AttributeValueRepository;
import com.firewise.admin.repository.PlantRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlantService {

    private final PlantRepository plantRepository;
    private final AttributeRepository attributeRepository;
    private final AttributeValueRepository attributeValueRepository;

    public PlantService(PlantRepository plantRepository,
                        AttributeRepository attributeRepository,
                        AttributeValueRepository attributeValueRepository) {
        this.plantRepository = plantRepository;
        this.attributeRepository = attributeRepository;
        this.attributeValueRepository = attributeValueRepository;
    }

    public Page<PlantSummaryDto> getPlants(String search, Pageable pageable) {
        Page<Plant> plants;
        if (search != null && !search.isBlank()) {
            plants = plantRepository.search(search.trim(), pageable);
        } else {
            plants = plantRepository.findAll(pageable);
        }

        int totalAttrs = (int) attributeRepository.count();

        return plants.map(p -> {
            List<String> populatedAttrIds = attributeValueRepository.findDistinctAttributeIdsByPlantId(p.getId());
            int completeness = totalAttrs > 0 ? (populatedAttrIds.size() * 100) / totalAttrs : 0;
            return new PlantSummaryDto(p.getId(), p.getGenus(), p.getSpecies(),
                    p.getSubspeciesVarieties(), p.getCommonName(), completeness, populatedAttrIds.size());
        });
    }

    public PlantDetailDto getPlantDetail(String plantId) {
        Plant plant = plantRepository.findById(plantId)
                .orElseThrow(() -> new ResourceNotFoundException("Plant not found: " + plantId));

        List<AttributeValue> values = attributeValueRepository.findByPlantId(plantId);
        List<Attribute> allAttributes = attributeRepository.findAll();

        Map<String, Attribute> attrMap = allAttributes.stream()
                .collect(Collectors.toMap(Attribute::getId, a -> a));

        // Group attributes by parent
        List<Attribute> topLevel = allAttributes.stream()
                .filter(a -> a.getParentAttributeId() == null)
                .sorted(Comparator.comparing(Attribute::getName))
                .toList();

        Map<String, List<Attribute>> childrenByParent = allAttributes.stream()
                .filter(a -> a.getParentAttributeId() != null)
                .collect(Collectors.groupingBy(Attribute::getParentAttributeId));

        // Group values by attributeId
        Map<String, List<AttributeValue>> valuesByAttr = values.stream()
                .collect(Collectors.groupingBy(AttributeValue::getAttributeId));

        Set<String> populatedAttrIds = values.stream()
                .map(AttributeValue::getAttributeId)
                .collect(Collectors.toSet());

        List<AttributeGroupDto> groups = new ArrayList<>();
        for (Attribute parent : topLevel) {
            AttributeGroupDto group = new AttributeGroupDto(parent.getId(), parent.getName());
            List<Attribute> children = childrenByParent.getOrDefault(parent.getId(), List.of());

            for (Attribute child : children) {
                List<AttributeValue> childValues = valuesByAttr.getOrDefault(child.getId(), List.of());
                for (AttributeValue av : childValues) {
                    AttributeValueDto dto = new AttributeValueDto();
                    dto.setValueId(av.getId());
                    dto.setAttributeId(child.getId());
                    dto.setAttributeName(child.getName());
                    dto.setValue(av.getValue());
                    dto.setValueType(child.getValueType());
                    dto.setSelectionType(child.getSelectionType());
                    dto.setValuesAllowed(child.getValuesAllowed());
                    dto.setSourceId(av.getSourceId());
                    dto.setSourceValue(av.getSourceValue());
                    dto.setNotes(av.getNotes());
                    group.getValues().add(dto);
                }
            }
            // Also include values directly on the parent attribute
            List<AttributeValue> parentValues = valuesByAttr.getOrDefault(parent.getId(), List.of());
            for (AttributeValue av : parentValues) {
                AttributeValueDto dto = new AttributeValueDto();
                dto.setValueId(av.getId());
                dto.setAttributeId(parent.getId());
                dto.setAttributeName(parent.getName());
                dto.setValue(av.getValue());
                dto.setValueType(parent.getValueType());
                dto.setSelectionType(parent.getSelectionType());
                dto.setValuesAllowed(parent.getValuesAllowed());
                dto.setSourceId(av.getSourceId());
                dto.setSourceValue(av.getSourceValue());
                dto.setNotes(av.getNotes());
                group.getValues().add(dto);
            }

            groups.add(group);
        }

        int totalAttrs = allAttributes.size();
        int populated = populatedAttrIds.size();
        int completeness = totalAttrs > 0 ? (populated * 100) / totalAttrs : 0;

        PlantDetailDto dto = new PlantDetailDto();
        dto.setId(plant.getId());
        dto.setGenus(plant.getGenus());
        dto.setSpecies(plant.getSpecies());
        dto.setSubspeciesVarieties(plant.getSubspeciesVarieties());
        dto.setCommonName(plant.getCommonName());
        dto.setNotes(plant.getNotes());
        dto.setLastUpdated(plant.getLastUpdated());
        dto.setUrls(plant.getUrls());
        dto.setCompletenessPercent(completeness);
        dto.setTotalAttributes(totalAttrs);
        dto.setPopulatedAttributes(populated);
        dto.setAttributeGroups(groups);

        return dto;
    }

    public Plant createPlant(Plant plant) {
        if (plant.getId() == null || plant.getId().isBlank()) {
            plant.setId(UUID.randomUUID().toString());
        }
        return plantRepository.save(plant);
    }

    public Plant updatePlant(String id, Plant updates) {
        Plant plant = plantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plant not found: " + id));
        if (updates.getGenus() != null) plant.setGenus(updates.getGenus());
        if (updates.getSpecies() != null) plant.setSpecies(updates.getSpecies());
        if (updates.getSubspeciesVarieties() != null) plant.setSubspeciesVarieties(updates.getSubspeciesVarieties());
        if (updates.getCommonName() != null) plant.setCommonName(updates.getCommonName());
        if (updates.getNotes() != null) plant.setNotes(updates.getNotes());
        return plantRepository.save(plant);
    }

    public void deletePlant(String id) {
        if (!plantRepository.existsById(id)) {
            throw new ResourceNotFoundException("Plant not found: " + id);
        }
        plantRepository.deleteById(id);
    }
}
