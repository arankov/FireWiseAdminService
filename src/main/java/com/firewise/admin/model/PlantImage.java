package com.firewise.admin.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "plant_images")
public class PlantImage {

    @Id
    private String id;

    @Column(name = "plant_id", nullable = false)
    private String plantId;

    @Column(nullable = false)
    private String source;

    @Column(name = "source_id")
    private String sourceId;

    @Column(name = "source_slug")
    private String sourceSlug;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "image_type")
    private String imageType;

    @Column(name = "is_primary")
    private Boolean isPrimary;

    private String copyright;

    @Column(name = "fetched_at")
    private String fetchedAt;

    @Column(name = "verified_at")
    private String verifiedAt;

    @Column(name = "match_score")
    private Integer matchScore;

    @Column(name = "match_field")
    private String matchField;

    @Column(name = "needs_verification")
    private Boolean needsVerification;

    @Column(name = "verified_by")
    private String verifiedBy;

    public PlantImage() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPlantId() { return plantId; }
    public void setPlantId(String plantId) { this.plantId = plantId; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getSourceId() { return sourceId; }
    public void setSourceId(String sourceId) { this.sourceId = sourceId; }

    public String getSourceSlug() { return sourceSlug; }
    public void setSourceSlug(String sourceSlug) { this.sourceSlug = sourceSlug; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getImageType() { return imageType; }
    public void setImageType(String imageType) { this.imageType = imageType; }

    public Boolean getIsPrimary() { return isPrimary; }
    public void setIsPrimary(Boolean isPrimary) { this.isPrimary = isPrimary; }

    public String getCopyright() { return copyright; }
    public void setCopyright(String copyright) { this.copyright = copyright; }

    public String getFetchedAt() { return fetchedAt; }
    public void setFetchedAt(String fetchedAt) { this.fetchedAt = fetchedAt; }

    public String getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(String verifiedAt) { this.verifiedAt = verifiedAt; }

    public Integer getMatchScore() { return matchScore; }
    public void setMatchScore(Integer matchScore) { this.matchScore = matchScore; }

    public String getMatchField() { return matchField; }
    public void setMatchField(String matchField) { this.matchField = matchField; }

    public Boolean getNeedsVerification() { return needsVerification; }
    public void setNeedsVerification(Boolean needsVerification) { this.needsVerification = needsVerification; }

    public String getVerifiedBy() { return verifiedBy; }
    public void setVerifiedBy(String verifiedBy) { this.verifiedBy = verifiedBy; }
}
