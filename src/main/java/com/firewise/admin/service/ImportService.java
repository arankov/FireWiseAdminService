package com.firewise.admin.service;

import com.firewise.admin.dto.QueueEntryDto;
import com.firewise.admin.exception.ResourceNotFoundException;
import com.firewise.admin.model.Attribute;
import com.firewise.admin.model.Plant;
import com.firewise.admin.model.QueueEntry;
import com.firewise.admin.model.QueueStatus;
import com.firewise.admin.repository.AttributeRepository;
import com.firewise.admin.repository.PlantRepository;
import com.firewise.admin.repository.SourceRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ImportService {

    private final PlantRepository plantRepository;
    private final AttributeRepository attributeRepository;
    private final SourceRepository sourceRepository;
    private final QueueService queueService;

    public ImportService(PlantRepository plantRepository,
                         AttributeRepository attributeRepository,
                         SourceRepository sourceRepository,
                         QueueService queueService) {
        this.plantRepository = plantRepository;
        this.attributeRepository = attributeRepository;
        this.sourceRepository = sourceRepository;
        this.queueService = queueService;
    }

    public List<QueueEntryDto> simulateImport(String sourceId, int count) {
        sourceRepository.findById(sourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Source not found: " + sourceId));

        List<Plant> allPlants = plantRepository.findAll();
        List<Attribute> allAttributes = attributeRepository.findAll();
        List<Attribute> leafAttributes = allAttributes.stream()
                .filter(a -> a.getParentAttributeId() != null)
                .toList();

        int totalAttrs = leafAttributes.size();
        Random random = new Random();
        List<QueueEntryDto> results = new ArrayList<>();

        int actualCount = Math.min(count, allPlants.size());
        List<Plant> shuffled = new ArrayList<>(allPlants);
        Collections.shuffle(shuffled, random);

        for (int i = 0; i < actualCount; i++) {
            Plant plant = shuffled.get(i);

            // Randomly populate 30-70% of attributes
            double completionRate = 0.3 + random.nextDouble() * 0.4;
            int populated = (int) (totalAttrs * completionRate);

            List<Attribute> shuffledAttrs = new ArrayList<>(leafAttributes);
            Collections.shuffle(shuffledAttrs, random);
            List<Attribute> selectedAttrs = shuffledAttrs.subList(0, Math.min(populated, shuffledAttrs.size()));

            StringBuilder attrJson = new StringBuilder();
            attrJson.append("{");
            boolean first = true;
            for (Attribute attr : selectedAttrs) {
                if (!first) attrJson.append(",");
                String sampleValue = generateSampleValue(attr, random);
                attrJson.append(String.format("\"%s\":{\"value\":\"%s\"}", attr.getId(), sampleValue));
                first = false;
            }
            attrJson.append("}");

            List<String> missingIds = leafAttributes.stream()
                    .filter(a -> !selectedAttrs.contains(a))
                    .map(Attribute::getId)
                    .toList();

            String proposedData = String.format(
                    "{\"genus\":\"%s\",\"species\":\"%s\",\"commonName\":\"%s\",\"attributes\":%s}",
                    plant.getGenus(), plant.getSpecies(),
                    plant.getCommonName() != null ? plant.getCommonName().replace("\"", "\\\"") : "",
                    attrJson);

            QueueEntry entry = new QueueEntry();
            entry.setPlantId(plant.getId());
            entry.setPlantGenus(plant.getGenus());
            entry.setPlantSpecies(plant.getSpecies());
            entry.setPlantCommonName(plant.getCommonName());
            entry.setSourceId(sourceId);
            entry.setStatus(QueueStatus.NEW);
            entry.setProposedData(proposedData);
            entry.setMissingAttributes(missingIds.toString());
            entry.setTotalAttributes(totalAttrs);
            entry.setPopulatedAttributes(populated);

            results.add(queueService.createQueueEntry(entry));
        }

        return results;
    }

    private String generateSampleValue(Attribute attr, Random random) {
        if ("boolean".equals(attr.getValueType())) {
            return random.nextBoolean() ? "true" : "false";
        }
        if ("numeric".equals(attr.getValueType())) {
            return String.valueOf(1 + random.nextInt(20));
        }
        // For text types, generate a simple placeholder
        String[] sampleValues = {"Low", "Medium", "High", "Yes", "No", "Native", "Moderate"};
        return sampleValues[random.nextInt(sampleValues.length)];
    }
}
