package com.firewise.admin.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "attributes")
public class Attribute {

    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(name = "parent_attribute_id")
    private String parentAttributeId;

    @Column(name = "value_type", nullable = false)
    private String valueType;

    @Column(name = "values_allowed", length = 4000)
    private String valuesAllowed;

    @Column(name = "value_units")
    private String valueUnits;

    @Column(length = 2000)
    private String notes;

    @Column(name = "selection_type")
    private String selectionType;

    public Attribute() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getParentAttributeId() { return parentAttributeId; }
    public void setParentAttributeId(String parentAttributeId) { this.parentAttributeId = parentAttributeId; }

    public String getValueType() { return valueType; }
    public void setValueType(String valueType) { this.valueType = valueType; }

    public String getValuesAllowed() { return valuesAllowed; }
    public void setValuesAllowed(String valuesAllowed) { this.valuesAllowed = valuesAllowed; }

    public String getValueUnits() { return valueUnits; }
    public void setValueUnits(String valueUnits) { this.valueUnits = valueUnits; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getSelectionType() { return selectionType; }
    public void setSelectionType(String selectionType) { this.selectionType = selectionType; }
}
