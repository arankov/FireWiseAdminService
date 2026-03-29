package com.firewise.admin.dto;

public class PlantSummaryDto {

    private String id;
    private String genus;
    private String species;
    private String subspeciesVarieties;
    private String commonName;
    private int completenessPercent;
    private int attributeCount;

    public PlantSummaryDto() {}

    public PlantSummaryDto(String id, String genus, String species, String subspeciesVarieties,
                           String commonName, int completenessPercent, int attributeCount) {
        this.id = id;
        this.genus = genus;
        this.species = species;
        this.subspeciesVarieties = subspeciesVarieties;
        this.commonName = commonName;
        this.completenessPercent = completenessPercent;
        this.attributeCount = attributeCount;
    }

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

    public int getCompletenessPercent() { return completenessPercent; }
    public void setCompletenessPercent(int completenessPercent) { this.completenessPercent = completenessPercent; }

    public int getAttributeCount() { return attributeCount; }
    public void setAttributeCount(int attributeCount) { this.attributeCount = attributeCount; }
}
