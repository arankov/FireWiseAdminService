package com.firewise.admin.dto;

import java.util.ArrayList;
import java.util.List;

public class AttributeGroupDto {

    private String groupId;
    private String groupName;
    private List<AttributeValueDto> values = new ArrayList<>();

    public AttributeGroupDto() {}

    public AttributeGroupDto(String groupId, String groupName) {
        this.groupId = groupId;
        this.groupName = groupName;
    }

    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    public List<AttributeValueDto> getValues() { return values; }
    public void setValues(List<AttributeValueDto> values) { this.values = values; }
}
