package com.firewise.admin.dto;

import java.util.ArrayList;
import java.util.List;

public class PlantDetailDto {

    private String id;
    private String genus;
    private String species;
    private String subspeciesVarieties;
    private String commonName;
    private String notes;
    private String lastUpdated;
    private String urls;
    private int completenessPercent;
    private int totalAttributes;
    private int populatedAttributes;
    private List<AttributeGroupDto> attributeGroups = new ArrayList<>();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getGenus() { return genus; }
    public void setGenus(String genus) { this.genus = genus; }

    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }

    public String getSubspeciesVarieties() { return subspeciesVarieties; }
    public void setSubspeciesVarieties(String subspeciesVarieties) { this.subspeciesVarieties = subspeciesVarieties; }

    public String getCommonName() { return commonName; }
    public void setCommonName(String commonName) { this.commonName = commonName; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(String lastUpdated) { this.lastUpdated = lastUpdated; }

    public String getUrls() { return urls; }
    public void setUrls(String urls) { this.urls = urls; }

    public int getCompletenessPercent() { return completenessPercent; }
    public void setCompletenessPercent(int completenessPercent) { this.completenessPercent = completenessPercent; }

    public int getTotalAttributes() { return totalAttributes; }
    public void setTotalAttributes(int totalAttributes) { this.totalAttributes = totalAttributes; }

    public int getPopulatedAttributes() { return populatedAttributes; }
    public void setPopulatedAttributes(int populatedAttributes) { this.populatedAttributes = populatedAttributes; }

    public List<AttributeGroupDto> getAttributeGroups() { return attributeGroups; }
    public void setAttributeGroups(List<AttributeGroupDto> attributeGroups) { this.attributeGroups = attributeGroups; }
}
