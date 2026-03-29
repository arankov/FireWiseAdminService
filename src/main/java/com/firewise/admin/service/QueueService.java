package com.firewise.admin.service;

import com.firewise.admin.dto.QueueEntryDto;
import com.firewise.admin.exception.InvalidStatusTransitionException;
import com.firewise.admin.exception.ResourceNotFoundException;
import com.firewise.admin.model.QueueEntry;
import com.firewise.admin.model.QueueStatus;
import com.firewise.admin.model.Source;
import com.firewise.admin.repository.QueueEntryRepository;
import com.firewise.admin.repository.SourceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
public class QueueService {

    private static final Map<QueueStatus, Set<QueueStatus>> ALLOWED_TRANSITIONS = Map.of(
            QueueStatus.NEW, Set.of(QueueStatus.WIP, QueueStatus.REJECTED),
            QueueStatus.WIP, Set.of(QueueStatus.APPROVED, QueueStatus.REJECTED),
            QueueStatus.REJECTED, Set.of(QueueStatus.WIP),
            QueueStatus.APPROVED, Set.of()
    );

    private final QueueEntryRepository queueEntryRepository;
    private final SourceRepository sourceRepository;

    public QueueService(QueueEntryRepository queueEntryRepository, SourceRepository sourceRepository) {
        this.queueEntryRepository = queueEntryRepository;
        this.sourceRepository = sourceRepository;
    }

    public Page<QueueEntryDto> getQueueEntries(QueueStatus status, String sourceId, Pageable pageable) {
        Page<QueueEntry> entries;
        if (status != null && sourceId != null) {
            entries = queueEntryRepository.findByStatusAndSourceId(status, sourceId, pageable);
        } else if (status != null) {
            entries = queueEntryRepository.findByStatus(status, pageable);
        } else if (sourceId != null) {
            entries = queueEntryRepository.findBySourceId(sourceId, pageable);
        } else {
            entries = queueEntryRepository.findAll(pageable);
        }
        return entries.map(this::toDto);
    }

    public QueueEntryDto getQueueEntry(String id) {
        QueueEntry entry = queueEntryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Queue entry not found: " + id));
        return toDto(entry);
    }

    public QueueEntryDto createQueueEntry(QueueEntry entry) {
        if (entry.getStatus() == null) {
            entry.setStatus(QueueStatus.NEW);
        }
        return toDto(queueEntryRepository.save(entry));
    }

    public QueueEntryDto updateQueueEntry(String id, QueueEntry updates) {
        QueueEntry entry = queueEntryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Queue entry not found: " + id));

        if (entry.getStatus() == QueueStatus.APPROVED) {
            throw new InvalidStatusTransitionException("Cannot edit an APPROVED entry");
        }

        if (updates.getProposedData() != null) entry.setProposedData(updates.getProposedData());
        if (updates.getMissingAttributes() != null) entry.setMissingAttributes(updates.getMissingAttributes());
        if (updates.getPopulatedAttributes() != null) entry.setPopulatedAttributes(updates.getPopulatedAttributes());
        if (updates.getAdminNotes() != null) entry.setAdminNotes(updates.getAdminNotes());
        if (updates.getPlantGenus() != null) entry.setPlantGenus(updates.getPlantGenus());
        if (updates.getPlantSpecies() != null) entry.setPlantSpecies(updates.getPlantSpecies());
        if (updates.getPlantCommonName() != null) entry.setPlantCommonName(updates.getPlantCommonName());

        return toDto(queueEntryRepository.save(entry));
    }

    public QueueEntryDto changeStatus(String id, QueueStatus newStatus) {
        QueueEntry entry = queueEntryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Queue entry not found: " + id));

        Set<QueueStatus> allowed = ALLOWED_TRANSITIONS.getOrDefault(entry.getStatus(), Set.of());
        if (!allowed.contains(newStatus)) {
            throw new InvalidStatusTransitionException(
                    "Cannot transition from " + entry.getStatus() + " to " + newStatus);
        }

        entry.setStatus(newStatus);
        return toDto(queueEntryRepository.save(entry));
    }

    public long countByStatus(QueueStatus status) {
        return queueEntryRepository.countByStatus(status);
    }

    private QueueEntryDto toDto(QueueEntry entry) {
        QueueEntryDto dto = new QueueEntryDto();
        dto.setId(entry.getId());
        dto.setPlantId(entry.getPlantId());
        dto.setPlantGenus(entry.getPlantGenus());
        dto.setPlantSpecies(entry.getPlantSpecies());
        dto.setPlantCommonName(entry.getPlantCommonName());
        dto.setSourceId(entry.getSourceId());
        dto.setStatus(entry.getStatus());
        dto.setProposedData(entry.getProposedData());
        dto.setMissingAttributes(entry.getMissingAttributes());
        dto.setTotalAttributes(entry.getTotalAttributes());
        dto.setPopulatedAttributes(entry.getPopulatedAttributes());
        dto.setAdminNotes(entry.getAdminNotes());
        dto.setCreatedAt(entry.getCreatedAt());
        dto.setUpdatedAt(entry.getUpdatedAt());
        dto.setReviewedBy(entry.getReviewedBy());

        if (entry.getTotalAttributes() != null && entry.getTotalAttributes() > 0 && entry.getPopulatedAttributes() != null) {
            dto.setCompletenessPercent((entry.getPopulatedAttributes() * 100) / entry.getTotalAttributes());
        }

        if (entry.getSourceId() != null) {
            sourceRepository.findById(entry.getSourceId()).ifPresent(s -> dto.setSourceName(s.getName()));
        }

        return dto;
    }
}
