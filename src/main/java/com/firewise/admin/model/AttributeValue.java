package com.firewise.admin.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "attribute_values")
public class AttributeValue {

    @Id
    private String id;

    @Column(name = "attribute_id", nullable = false)
    private String attributeId;

    @Column(name = "plant_id", nullable = false)
    private String plantId;

    @Column(name = "\"value\"", length = 2000)
    private String value;

    @Column(name = "source_id")
    private String sourceId;

    @Column(length = 2000)
    private String notes;

    @Column(name = "source_value", length = 2000)
    private String sourceValue;

    @Column(length = 4000)
    private String metadata;

    @Column(length = 4000)
    private String urls;

    public AttributeValue() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAttributeId() { return attributeId; }
    public void setAttributeId(String attributeId) { this.attributeId = attributeId; }

    public String getPlantId() { return plantId; }
    public void setPlantId(String plantId) { this.plantId = plantId; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    public String getSourceId() { return sourceId; }
    public void setSourceId(String sourceId) { this.sourceId = sourceId; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getSourceValue() { return sourceValue; }
    public void setSourceValue(String sourceValue) { this.sourceValue = sourceValue; }

    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }

    public String getUrls() { return urls; }
    public void setUrls(String urls) { this.urls = urls; }
}
