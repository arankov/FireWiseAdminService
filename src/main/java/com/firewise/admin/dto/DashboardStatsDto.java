package com.firewise.admin.dto;

import java.util.List;
import java.util.Map;

public class DashboardStatsDto {

    private long totalPlants;
    private long totalSources;
    private long totalAttributes;
    private Map<String, Long> queueCounts;
    private List<ChangelogEntryDto> recentActivity;

    public long getTotalPlants() { return totalPlants; }
    public void setTotalPlants(long totalPlants) { this.totalPlants = totalPlants; }

    public long getTotalSources() { return totalSources; }
    public void setTotalSources(long totalSources) { this.totalSources = totalSources; }

    public long getTotalAttributes() { return totalAttributes; }
    public void setTotalAttributes(long totalAttributes) { this.totalAttributes = totalAttributes; }

    public Map<String, Long> getQueueCounts() { return queueCounts; }
    public void setQueueCounts(Map<String, Long> queueCounts) { this.queueCounts = queueCounts; }

    public List<ChangelogEntryDto> getRecentActivity() { return recentActivity; }
    public void setRecentActivity(List<ChangelogEntryDto> recentActivity) { this.recentActivity = recentActivity; }

    public static class ChangelogEntryDto {
        private String id;
        private String plantId;
        private String plantName;
        private String field;
        private String changeType;
        private String changedBy;
        private String createdAt;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getPlantId() { return plantId; }
        public void setPlantId(String plantId) { this.plantId = plantId; }

        public String getPlantName() { return plantName; }
        public void setPlantName(String plantName) { this.plantName = plantName; }

        public String getField() { return field; }
        public void setField(String field) { this.field = field; }

        public String getChangeType() { return changeType; }
        public void setChangeType(String changeType) { this.changeType = changeType; }

        public String getChangedBy() { return changedBy; }
        public void setChangedBy(String changedBy) { this.changedBy = changedBy; }

        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    }
}
