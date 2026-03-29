package com.firewise.admin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.firewise.admin.model.*;
import com.firewise.admin.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class DataSeeder implements CommandLineRunner {

    private final PlantRepository plantRepository;
    private final AttributeRepository attributeRepository;
    private final SourceRepository sourceRepository;
    private final AttributeValueRepository attributeValueRepository;
    private final QueueEntryRepository queueEntryRepository;
    private final ObjectMapper objectMapper;

    public DataSeeder(PlantRepository plantRepository,
                      AttributeRepository attributeRepository,
                      SourceRepository sourceRepository,
                      AttributeValueRepository attributeValueRepository,
                      QueueEntryRepository queueEntryRepository) {
        this.plantRepository = plantRepository;
        this.attributeRepository = attributeRepository;
        this.sourceRepository = sourceRepository;
        this.attributeValueRepository = attributeValueRepository;
        this.queueEntryRepository = queueEntryRepository;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void run(String... args) throws Exception {
        if (plantRepository.count() > 0) {
            System.out.println("Database already seeded with " + plantRepository.count() + " plants. Skipping.");
            return;
        }

        System.out.println("Seeding database from JSON files...");

        loadSources();
        loadAttributes();
        loadPlants();
        loadValues();
        createSampleQueueEntries();

        System.out.println("Seeding complete.");
    }

    private void loadSources() throws Exception {
        try (InputStream is = new ClassPathResource("seed/sources.json").getInputStream()) {
            List<JsonNode> nodes = objectMapper.readValue(is, new TypeReference<>() {});
            for (JsonNode node : nodes) {
                Source source = new Source();
                source.setId(text(node, "id"));
                source.setName(text(node, "name"));
                source.setUrl(text(node, "url"));
                source.setAddress(text(node, "address"));
                source.setPhone(text(node, "phone"));
                source.setNotes(text(node, "notes"));
                source.setRegion(text(node, "region"));
                source.setTargetLocation(text(node, "target_location"));
                source.setTopicsAddressed(text(node, "topics_addressed"));
                source.setAttribution(text(node, "attribution"));
                source.setRefCode(text(node, "ref_code"));
                source.setFileLink(text(node, "file_link"));
                sourceRepository.save(source);
            }
            System.out.println("  Loaded " + sourceRepository.count() + " sources");
        }
    }

    private void loadAttributes() throws Exception {
        try (InputStream is = new ClassPathResource("seed/attributes.json").getInputStream()) {
            List<JsonNode> nodes = objectMapper.readValue(is, new TypeReference<>() {});
            for (JsonNode node : nodes) {
                Attribute attr = new Attribute();
                attr.setId(text(node, "id"));
                attr.setName(text(node, "name"));
                attr.setParentAttributeId(text(node, "parent_attribute_id"));
                attr.setValueType(text(node, "value_type"));
                JsonNode va = node.get("values_allowed");
                if (va != null && !va.isNull()) {
                    attr.setValuesAllowed(va.toString());
                }
                attr.setValueUnits(text(node, "value_units"));
                attr.setNotes(text(node, "notes"));
                attr.setSelectionType(text(node, "selection_type"));
                attributeRepository.save(attr);
            }
            System.out.println("  Loaded " + attributeRepository.count() + " attributes");
        }
    }

    private void loadPlants() throws Exception {
        try (InputStream is = new ClassPathResource("seed/plants.json").getInputStream()) {
            List<JsonNode> nodes = objectMapper.readValue(is, new TypeReference<>() {});
            for (JsonNode node : nodes) {
                Plant plant = new Plant();
                plant.setId(text(node, "id"));
                plant.setGenus(text(node, "genus"));
                plant.setSpecies(text(node, "species"));
                plant.setSubspeciesVarieties(text(node, "subspecies_varieties"));
                plant.setCommonName(text(node, "common_name"));
                plant.setNotes(text(node, "notes"));
                plant.setLastUpdated(text(node, "last_updated"));
                JsonNode urlsNode = node.get("urls");
                if (urlsNode != null && !urlsNode.isNull()) {
                    plant.setUrls(urlsNode.toString());
                }
                plantRepository.save(plant);
            }
            System.out.println("  Loaded " + plantRepository.count() + " plants");
        }
    }

    private void loadValues() throws Exception {
        try (InputStream is = new ClassPathResource("seed/values.json").getInputStream()) {
            List<JsonNode> nodes = objectMapper.readValue(is, new TypeReference<>() {});
            int count = 0;
            for (JsonNode node : nodes) {
                AttributeValue av = new AttributeValue();
                av.setId(text(node, "id"));
                av.setAttributeId(text(node, "attribute_id"));
                av.setPlantId(text(node, "plant_id"));
                av.setValue(text(node, "value"));
                av.setSourceId(text(node, "source_id"));
                av.setNotes(text(node, "notes"));
                av.setSourceValue(text(node, "source_value"));
                JsonNode meta = node.get("metadata");
                if (meta != null && !meta.isNull()) {
                    av.setMetadata(meta.toString());
                }
                JsonNode urls = node.get("urls");
                if (urls != null && !urls.isNull()) {
                    av.setUrls(urls.toString());
                }
                attributeValueRepository.save(av);
                count++;
            }
            System.out.println("  Loaded " + count + " attribute values");
        }
    }

    private void createSampleQueueEntries() {
        List<Plant> plants = plantRepository.findAll();
        List<Source> sources = sourceRepository.findAll();
        if (plants.size() < 8 || sources.isEmpty()) return;

        int totalAttrs = (int) attributeRepository.count();
        QueueStatus[] statuses = {
            QueueStatus.NEW, QueueStatus.NEW,
            QueueStatus.WIP, QueueStatus.WIP, QueueStatus.WIP,
            QueueStatus.APPROVED, QueueStatus.APPROVED,
            QueueStatus.REJECTED
        };

        for (int i = 0; i < 8; i++) {
            Plant plant = plants.get(i);
            Source source = sources.get(i % sources.size());
            int populated = (int) (totalAttrs * (0.3 + Math.random() * 0.5));

            QueueEntry entry = new QueueEntry();
            entry.setPlantId(plant.getId());
            entry.setPlantGenus(plant.getGenus());
            entry.setPlantSpecies(plant.getSpecies());
            entry.setPlantCommonName(plant.getCommonName());
            entry.setSourceId(source.getId());
            entry.setStatus(statuses[i]);
            entry.setTotalAttributes(totalAttrs);
            entry.setPopulatedAttributes(populated);

            String proposedData = String.format(
                "{\"genus\":\"%s\",\"species\":\"%s\",\"commonName\":\"%s\",\"attributes\":{}}",
                plant.getGenus(), plant.getSpecies(),
                plant.getCommonName() != null ? plant.getCommonName() : "");
            entry.setProposedData(proposedData);
            entry.setMissingAttributes("[]");

            if (statuses[i] == QueueStatus.WIP) {
                entry.setAdminNotes("Under review - verifying fire resistance data");
            } else if (statuses[i] == QueueStatus.REJECTED) {
                entry.setAdminNotes("Insufficient data from source - needs additional verification");
                entry.setReviewedBy("admin");
            } else if (statuses[i] == QueueStatus.APPROVED) {
                entry.setReviewedBy("admin");
            }

            queueEntryRepository.save(entry);
        }
        System.out.println("  Created 8 sample queue entries");
    }

    private String text(JsonNode node, String field) {
        JsonNode val = node.get(field);
        if (val == null || val.isNull()) return null;
        return val.asText();
    }
}
