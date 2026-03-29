package com.firewise.admin.dto;

public class ClaudeSuggestionDto {

    private String attributeId;
    private String attributeName;
    private String suggestedValue;
    private String confidence;
    private String reasoning;

    public ClaudeSuggestionDto() {}

    public ClaudeSuggestionDto(String attributeId, String attributeName, String suggestedValue, String confidence, String reasoning) {
        this.attributeId = attributeId;
        this.attributeName = attributeName;
        this.suggestedValue = suggestedValue;
        this.confidence = confidence;
        this.reasoning = reasoning;
    }

    public String getAttributeId() { return attributeId; }
    public void setAttributeId(String attributeId) { this.attributeId = attributeId; }

    public String getAttributeName() { return attributeName; }
    public void setAttributeName(String attributeName) { this.attributeName = attributeName; }

    public String getSuggestedValue() { return suggestedValue; }
    public void setSuggestedValue(String suggestedValue) { this.suggestedValue = suggestedValue; }

    public String getConfidence() { return confidence; }
    public void setConfidence(String confidence) { this.confidence = confidence; }

    public String getReasoning() { return reasoning; }
    public void setReasoning(String reasoning) { this.reasoning = reasoning; }
}
