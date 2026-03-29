package com.firewise.admin.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "queue_entries")
public class QueueEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "plant_id")
    private String plantId;

    @Column(name = "plant_genus")
    private String plantGenus;

    @Column(name = "plant_species")
    private String plantSpecies;

    @Column(name = "plant_common_name")
    private String plantCommonName;

    @Column(name = "source_id")
    private String sourceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QueueStatus status;

    @Column(name = "proposed_data", length = 10000)
    private String proposedData;

    @Column(name = "missing_attributes", length = 4000)
    private String missingAttributes;

    @Column(name = "total_attributes")
    private Integer totalAttributes;

    @Column(name = "populated_attributes")
    private Integer populatedAttributes;

    @Column(name = "admin_notes", length = 2000)
    private String adminNotes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "reviewed_by")
    private String reviewedBy;

    public QueueEntry() {}

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPlantId() { return plantId; }
    public void setPlantId(String plantId) { this.plantId = plantId; }

    public String getPlantGenus() { return plantGenus; }
    public void setPlantGenus(String plantGenus) { this.plantGenus = plantGenus; }

    public String getPlantSpecies() { return plantSpecies; }
    public void setPlantSpecies(String plantSpecies) { this.plantSpecies = plantSpecies; }

    public String getPlantCommonName() { return plantCommonName; }
    public void setPlantCommonName(String plantCommonName) { this.plantCommonName = plantCommonName; }

    public String getSourceId() { return sourceId; }
    public void setSourceId(String sourceId) { this.sourceId = sourceId; }

    public QueueStatus getStatus() { return status; }
    public void setStatus(QueueStatus status) { this.status = status; }

    public String getProposedData() { return proposedData; }
    public void setProposedData(String proposedData) { this.proposedData = proposedData; }

    public String getMissingAttributes() { return missingAttributes; }
    public void setMissingAttributes(String missingAttributes) { this.missingAttributes = missingAttributes; }

    public Integer getTotalAttributes() { return totalAttributes; }
    public void setTotalAttributes(Integer totalAttributes) { this.totalAttributes = totalAttributes; }

    public Integer getPopulatedAttributes() { return populatedAttributes; }
    public void setPopulatedAttributes(Integer populatedAttributes) { this.populatedAttributes = populatedAttributes; }

    public String getAdminNotes() { return adminNotes; }
    public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(String reviewedBy) { this.reviewedBy = reviewedBy; }
}
