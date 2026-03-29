package com.firewise.admin.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "plants")
public class Plant {

    @Id
    private String id;

    @Column(nullable = false)
    private String genus;

    @Column(nullable = false)
    private String species;

    @Column(name = "subspecies_varieties", length = 500)
    private String subspeciesVarieties;

    @Column(name = "common_name", length = 500)
    private String commonName;

    @Column(length = 2000)
    private String notes;

    @Column(name = "last_updated")
    private String lastUpdated;

    @Column(length = 4000)
    private String urls;

    public Plant() {}

    public Plant(String id, String genus, String species, String commonName) {
        this.id = id;
        this.genus = genus;
        this.species = species;
        this.commonName = commonName;
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

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(String lastUpdated) { this.lastUpdated = lastUpdated; }

    public String getUrls() { return urls; }
    public void setUrls(String urls) { this.urls = urls; }
}
