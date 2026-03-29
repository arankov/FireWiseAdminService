package com.firewise.admin.dto;

import com.firewise.admin.model.QueueStatus;

import java.time.LocalDateTime;

public class QueueEntryDto {

    private String id;
    private String plantId;
    private String plantGenus;
    private String plantSpecies;
    private String plantCommonName;
    private String sourceId;
    private String sourceName;
    private QueueStatus status;
    private String proposedData;
    private String missingAttributes;
    private Integer totalAttributes;
    private Integer populatedAttributes;
    private int completenessPercent;
    private String adminNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String reviewedBy;

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

    public String getSourceName() { return sourceName; }
    public void setSourceName(String sourceName) { this.sourceName = sourceName; }

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

    public int getCompletenessPercent() { return completenessPercent; }
    public void setCompletenessPercent(int completenessPercent) { this.completenessPercent = completenessPercent; }

    public String getAdminNotes() { return adminNotes; }
    public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(String reviewedBy) { this.reviewedBy = reviewedBy; }
}
