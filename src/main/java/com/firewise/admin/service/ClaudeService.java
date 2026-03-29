package com.firewise.admin.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.firewise.admin.dto.ClaudeSuggestionDto;
import com.firewise.admin.model.Attribute;
import com.firewise.admin.model.AttributeValue;
import com.firewise.admin.model.QueueEntry;
import com.firewise.admin.model.Source;
import com.firewise.admin.repository.AttributeRepository;
import com.firewise.admin.repository.AttributeValueRepository;
import com.firewise.admin.repository.SourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ClaudeService {

    private static final Logger log = LoggerFactory.getLogger(ClaudeService.class);
    private static final String ANTHROPIC_API_URL = "https://api.anthropic.com/v1/messages";
    private static final String MODEL = "claude-sonnet-4-20250514";

    private final SourceRepository sourceRepository;
    private final AttributeRepository attributeRepository;
    private final AttributeValueRepository attributeValueRepository;
    private final ObjectMapper objectMapper;
    private final RestClient restClient;

    public ClaudeService(SourceRepository sourceRepository,
                         AttributeRepository attributeRepository,
                         AttributeValueRepository attributeValueRepository,
                         ObjectMapper objectMapper) {
        this.sourceRepository = sourceRepository;
        this.attributeRepository = attributeRepository;
        this.attributeValueRepository = attributeValueRepository;
        this.objectMapper = objectMapper;
        this.restClient = RestClient.create();
    }

    public List<ClaudeSuggestionDto> askClaude(String apiKey, QueueEntry entry) {
        String prompt = buildPrompt(entry);
        String response = callAnthropicApi(apiKey, prompt);
        return parseSuggestions(response);
    }

    private String buildPrompt(QueueEntry entry) {
        StringBuilder sb = new StringBuilder();

        sb.append("You are a plant biology expert helping populate a fire-resistance plant database.\n\n");
        sb.append("Plant: ").append(entry.getPlantGenus()).append(" ").append(entry.getPlantSpecies());
        if (entry.getPlantCommonName() != null) {
            sb.append(" (").append(entry.getPlantCommonName()).append(")");
        }
        sb.append("\n\n");

        // Add source descriptions
        List<Source> sources = sourceRepository.findAll();
        sb.append("Available data sources (use these to inform your suggestions):\n");
        for (Source s : sources) {
            sb.append("- ").append(s.getName());
            if (s.getRegion() != null) sb.append(" [Region: ").append(s.getRegion()).append("]");
            if (s.getTopicsAddressed() != null) sb.append(" Topics: ").append(s.getTopicsAddressed());
            if (s.getUrl() != null) sb.append(" URL: ").append(s.getUrl());
            sb.append("\n");
        }
        sb.append("\n");

        // Load all attributes
        List<Attribute> allAttributes = attributeRepository.findAll();
        Map<String, Attribute> attrMap = allAttributes.stream()
                .collect(Collectors.toMap(Attribute::getId, a -> a));

        // Determine populated attribute IDs from proposedData
        Set<String> populatedAttrIds = getPopulatedAttributeIds(entry);

        // Load existing attribute values if plant exists
        if (entry.getPlantId() != null) {
            List<AttributeValue> existingValues = attributeValueRepository.findByPlantId(entry.getPlantId());
            if (!existingValues.isEmpty()) {
                sb.append("Currently populated attributes:\n");
                for (AttributeValue av : existingValues) {
                    Attribute attr = attrMap.get(av.getAttributeId());
                    String attrName = attr != null ? attr.getName() : av.getAttributeId();
                    sb.append("- ").append(attrName).append(": ").append(av.getValue()).append("\n");
                    populatedAttrIds.add(av.getAttributeId());
                }
                sb.append("\n");
            }
        }

        // List missing attributes
        List<Attribute> missingAttrs = allAttributes.stream()
                .filter(a -> a.getParentAttributeId() != null) // only leaf attributes
                .filter(a -> !populatedAttrIds.contains(a.getId()))
                .toList();

        sb.append("Missing attributes that need values:\n");
        for (Attribute a : missingAttrs) {
            sb.append("- ").append(a.getName())
                    .append(" (id: ").append(a.getId())
                    .append(", type: ").append(a.getValueType());
            if (a.getValuesAllowed() != null && !a.getValuesAllowed().isEmpty()) {
                sb.append(", allowed values: ").append(a.getValuesAllowed());
            }
            if (a.getValueUnits() != null) {
                sb.append(", units: ").append(a.getValueUnits());
            }
            sb.append(")\n");
        }

        sb.append("\nFor each missing attribute, provide your best suggested value based on known plant biology ");
        sb.append("and the data sources listed. Return ONLY a JSON array with no other text:\n");
        sb.append("[{\"attributeId\": \"...\", \"attributeName\": \"...\", \"suggestedValue\": \"...\", ");
        sb.append("\"confidence\": \"HIGH|MEDIUM|LOW\", \"reasoning\": \"brief explanation\"}]");

        return sb.toString();
    }

    private Set<String> getPopulatedAttributeIds(QueueEntry entry) {
        try {
            if (entry.getProposedData() != null && !entry.getProposedData().isEmpty()) {
                Map<String, Object> proposed = objectMapper.readValue(entry.getProposedData(),
                        new TypeReference<Map<String, Object>>() {});
                return proposed.keySet().stream()
                        .filter(k -> proposed.get(k) != null && !proposed.get(k).toString().isEmpty())
                        .collect(Collectors.toSet());
            }
        } catch (Exception e) {
            log.warn("Failed to parse proposedData: {}", e.getMessage());
        }
        return new java.util.HashSet<>();
    }

    private String callAnthropicApi(String apiKey, String prompt) {
        Map<String, Object> requestBody = Map.of(
                "model", MODEL,
                "max_tokens", 4096,
                "messages", List.of(Map.of(
                        "role", "user",
                        "content", prompt
                ))
        );

        String responseJson = restClient.post()
                .uri(ANTHROPIC_API_URL)
                .header("x-api-key", apiKey)
                .header("anthropic-version", "2023-06-01")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(String.class);

        try {
            JsonNode root = objectMapper.readTree(responseJson);
            JsonNode content = root.path("content");
            if (content.isArray() && !content.isEmpty()) {
                return content.get(0).path("text").asText();
            }
        } catch (Exception e) {
            log.error("Failed to parse Anthropic API response: {}", e.getMessage());
        }

        return "[]";
    }

    private List<ClaudeSuggestionDto> parseSuggestions(String responseText) {
        try {
            // Extract JSON array from response (Claude may include markdown fencing)
            String json = responseText.trim();
            if (json.contains("[")) {
                json = json.substring(json.indexOf('['), json.lastIndexOf(']') + 1);
            }
            return objectMapper.readValue(json, new TypeReference<List<ClaudeSuggestionDto>>() {});
        } catch (Exception e) {
            log.error("Failed to parse Claude suggestions: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
}
