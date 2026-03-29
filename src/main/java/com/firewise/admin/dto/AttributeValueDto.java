package com.firewise.admin.dto;

public class AttributeValueDto {

    private String valueId;
    private String attributeId;
    private String attributeName;
    private String value;
    private String valueType;
    private String selectionType;
    private String valuesAllowed;
    private String sourceId;
    private String sourceValue;
    private String notes;

    public String getValueId() { return valueId; }
    public void setValueId(String valueId) { this.valueId = valueId; }

    public String getAttributeId() { return attributeId; }
    public void setAttributeId(String attributeId) { this.attributeId = attributeId; }

    public String getAttributeName() { return attributeName; }
    public void setAttributeName(String attributeName) { this.attributeName = attributeName; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    public String getValueType() { return valueType; }
    public void setValueType(String valueType) { this.valueType = valueType; }

    public String getSelectionType() { return selectionType; }
    public void setSelectionType(String selectionType) { this.selectionType = selectionType; }

    public String getValuesAllowed() { return valuesAllowed; }
    public void setValuesAllowed(String valuesAllowed) { this.valuesAllowed = valuesAllowed; }

    public String getSourceId() { return sourceId; }
    public void setSourceId(String sourceId) { this.sourceId = sourceId; }

    public String getSourceValue() { return sourceValue; }
    public void setSourceValue(String sourceValue) { this.sourceValue = sourceValue; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
