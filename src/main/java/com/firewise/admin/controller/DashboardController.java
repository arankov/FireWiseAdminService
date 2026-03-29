package com.firewise.admin.controller;

import com.firewise.admin.dto.DashboardStatsDto;
import com.firewise.admin.model.PlantChangelog;
import com.firewise.admin.model.QueueStatus;
import com.firewise.admin.repository.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final PlantRepository plantRepository;
    private final SourceRepository sourceRepository;
    private final AttributeRepository attributeRepository;
    private final QueueEntryRepository queueEntryRepository;
    private final PlantChangelogRepository changelogRepository;

    public DashboardController(PlantRepository plantRepository,
                               SourceRepository sourceRepository,
                               AttributeRepository attributeRepository,
                               QueueEntryRepository queueEntryRepository,
                               PlantChangelogRepository changelogRepository) {
        this.plantRepository = plantRepository;
        this.sourceRepository = sourceRepository;
        this.attributeRepository = attributeRepository;
        this.queueEntryRepository = queueEntryRepository;
        this.changelogRepository = changelogRepository;
    }

    @GetMapping("/stats")
    public DashboardStatsDto getStats() {
        DashboardStatsDto stats = new DashboardStatsDto();
        stats.setTotalPlants(plantRepository.count());
        stats.setTotalSources(sourceRepository.count());
        stats.setTotalAttributes(attributeRepository.count());

        Map<String, Long> queueCounts = new LinkedHashMap<>();
        for (QueueStatus status : QueueStatus.values()) {
            queueCounts.put(status.name(), queueEntryRepository.countByStatus(status));
        }
        stats.setQueueCounts(queueCounts);

        List<PlantChangelog> recent = changelogRepository
                .findAllByOrderByCreatedAtDesc(PageRequest.of(0, 10))
                .getContent();

        List<DashboardStatsDto.ChangelogEntryDto> activity = recent.stream().map(cl -> {
            DashboardStatsDto.ChangelogEntryDto dto = new DashboardStatsDto.ChangelogEntryDto();
            dto.setId(cl.getId());
            dto.setPlantId(cl.getPlantId());
            dto.setPlantName(cl.getPlantName());
            dto.setField(cl.getField());
            dto.setChangeType(cl.getChangeType());
            dto.setChangedBy(cl.getChangedBy());
            dto.setCreatedAt(cl.getCreatedAt());
            return dto;
        }).toList();
        stats.setRecentActivity(activity);

        return stats;
    }
}
