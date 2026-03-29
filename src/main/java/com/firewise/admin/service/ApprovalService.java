package com.firewise.admin.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.firewise.admin.dto.QueueEntryDto;
import com.firewise.admin.exception.InvalidStatusTransitionException;
import com.firewise.admin.exception.ResourceNotFoundException;
import com.firewise.admin.model.*;
import com.firewise.admin.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

@Service
public class ApprovalService {

    private final QueueEntryRepository queueEntryRepository;
    private final PlantRepository plantRepository;
    private final AttributeValueRepository attributeValueRepository;
    private final PlantChangelogRepository changelogRepository;
    private final QueueService queueService;
    private final ObjectMapper objectMapper;

    public ApprovalService(QueueEntryRepository queueEntryRepository,
                           PlantRepository plantRepository,
                           AttributeValueRepository attributeValueRepository,
                           PlantChangelogRepository changelogRepository,
                           QueueService queueService) {
        this.queueEntryRepository = queueEntryRepository;
        this.plantRepository = plantRepository;
        this.attributeValueRepository = attributeValueRepository;
        this.changelogRepository = changelogRepository;
        this.queueService = queueService;
        this.objectMapper = new ObjectMapper();
    }

    @Transactional
    public QueueEntryDto approve(String queueEntryId, String reviewedBy) {
        QueueEntry entry = queueEntryRepository.findById(queueEntryId)
                .orElseThrow(() -> new ResourceNotFoundException("Queue entry not found: " + queueEntryId));

        if (entry.getStatus() != QueueStatus.WIP && entry.getStatus() != QueueStatus.NEW) {
            throw new InvalidStatusTransitionException(
                    "Can only approve entries with status NEW or WIP, current: " + entry.getStatus());
        }

        try {
            JsonNode proposedData = objectMapper.readTree(entry.getProposedData());

            // Create or update plant
            Plant plant;
            boolean isNewPlant = entry.getPlantId() == null;

            if (isNewPlant) {
                plant = new Plant();
                plant.setId(UUID.randomUUID().toString());
            } else {
                plant = plantRepository.findById(entry.getPlantId())
                        .orElseGet(() -> {
                            Plant p = new Plant();
                            p.setId(entry.getPlantId());
                            return p;
                        });
            }

            // Update plant core fields from proposed data
            updatePlantField(plant, proposedData, "genus", entry, reviewedBy);
            updatePlantField(plant, proposedData, "species", entry, reviewedBy);
            updatePlantField(plant, proposedData, "commonName", entry, reviewedBy);

            if (plant.getGenus() == null) plant.setGenus(entry.getPlantGenus());
            if (plant.getSpecies() == null) plant.setSpecies(entry.getPlantSpecies());
            if (plant.getCommonName() == null) plant.setCommonName(entry.getPlantCommonName());

            plant.setLastUpdated(LocalDateTime.now().toString());
            plantRepository.save(plant);

            // Merge attribute values
            JsonNode attributes = proposedData.get("attributes");
            if (attributes != null && attributes.isObject()) {
                Iterator<Map.Entry<String, JsonNode>> fields = attributes.fields();
                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> attrEntry = fields.next();
                    String attributeId = attrEntry.getKey();
                    JsonNode attrData = attrEntry.getValue();

                    String newValue = attrData.has("value") ? attrData.get("value").asText() : null;
                    if (newValue == null) continue;

                    var existing = attributeValueRepository.findByPlantIdAndAttributeId(plant.getId(), attributeId);
                    String oldValue = existing.isEmpty() ? null : existing.get(0).getValue();

                    if (existing.isEmpty()) {
                        AttributeValue av = new AttributeValue();
                        av.setId(UUID.randomUUID().toString());
                        av.setAttributeId(attributeId);
                        av.setPlantId(plant.getId());
                        av.setValue(newValue);
                        av.setSourceId(entry.getSourceId());
                        if (attrData.has("sourceValue")) {
                            av.setSourceValue(attrData.get("sourceValue").asText());
                        }
                        attributeValueRepository.save(av);
                    } else {
                        AttributeValue av = existing.get(0);
                        av.setValue(newValue);
                        attributeValueRepository.save(av);
                    }

                    if (oldValue == null || !oldValue.equals(newValue)) {
                        PlantChangelog changelog = new PlantChangelog();
                        changelog.setId(UUID.randomUUID().toString());
                        changelog.setPlantId(plant.getId());
                        changelog.setPlantName(plant.getGenus() + " " + plant.getSpecies());
                        changelog.setField("attribute:" + attributeId);
                        changelog.setOldValue(oldValue);
                        changelog.setNewValue(newValue);
                        changelog.setChangeType(oldValue == null ? "add" : "update");
                        changelog.setChangedBy(reviewedBy != null ? reviewedBy : "admin");
                        changelogRepository.save(changelog);
                    }
                }
            }

            // Update queue entry
            entry.setStatus(QueueStatus.APPROVED);
            entry.setReviewedBy(reviewedBy != null ? reviewedBy : "admin");
            if (isNewPlant) {
                entry.setPlantId(plant.getId());
            }
            queueEntryRepository.save(entry);

        } catch (InvalidStatusTransitionException | ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to process approval: " + e.getMessage(), e);
        }

        return queueService.getQueueEntry(queueEntryId);
    }

    @Transactional
    public QueueEntryDto reject(String queueEntryId, String adminNotes, String reviewedBy) {
        QueueEntry entry = queueEntryRepository.findById(queueEntryId)
                .orElseThrow(() -> new ResourceNotFoundException("Queue entry not found: " + queueEntryId));

        if (entry.getStatus() == QueueStatus.APPROVED) {
            throw new InvalidStatusTransitionException("Cannot reject an APPROVED entry");
        }

        entry.setStatus(QueueStatus.REJECTED);
        entry.setAdminNotes(adminNotes);
        entry.setReviewedBy(reviewedBy != null ? reviewedBy : "admin");
        queueEntryRepository.save(entry);

        return queueService.getQueueEntry(queueEntryId);
    }

    private void updatePlantField(Plant plant, JsonNode data, String field, QueueEntry entry, String reviewedBy) {
        if (!data.has(field) || data.get(field).isNull()) return;

        String newValue = data.get(field).asText();
        String oldValue = switch (field) {
            case "genus" -> plant.getGenus();
            case "species" -> plant.getSpecies();
            case "commonName" -> plant.getCommonName();
            default -> null;
        };

        switch (field) {
            case "genus" -> plant.setGenus(newValue);
            case "species" -> plant.setSpecies(newValue);
            case "commonName" -> plant.setCommonName(newValue);
        }

        if (oldValue == null || !oldValue.equals(newValue)) {
            PlantChangelog changelog = new PlantChangelog();
            changelog.setId(UUID.randomUUID().toString());
            changelog.setPlantId(entry.getPlantId() != null ? entry.getPlantId() : "pending");
            changelog.setPlantName(entry.getPlantGenus() + " " + entry.getPlantSpecies());
            changelog.setField(field);
            changelog.setOldValue(oldValue);
            changelog.setNewValue(newValue);
            changelog.setChangeType(oldValue == null ? "add" : "update");
            changelog.setChangedBy(reviewedBy != null ? reviewedBy : "admin");
            changelogRepository.save(changelog);
        }
    }
}
