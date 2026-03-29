package com.firewise.admin.dto;

import java.util.ArrayList;
import java.util.List;

public class AttributeTreeDto {

    private String id;
    private String name;
    private String valueType;
    private String selectionType;
    private String valuesAllowed;
    private List<AttributeTreeDto> children = new ArrayList<>();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getValueType() { return valueType; }
    public void setValueType(String valueType) { this.valueType = valueType; }

    public String getSelectionType() { return selectionType; }
    public void setSelectionType(String selectionType) { this.selectionType = selectionType; }

    public String getValuesAllowed() { return valuesAllowed; }
    public void setValuesAllowed(String valuesAllowed) { this.valuesAllowed = valuesAllowed; }

    public List<AttributeTreeDto> getChildren() { return children; }
    public void setChildren(List<AttributeTreeDto> children) { this.children = children; }
}
