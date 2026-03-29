package com.firewise.admin.repository;

import com.firewise.admin.model.QueueEntry;
import com.firewise.admin.model.QueueStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QueueEntryRepository extends JpaRepository<QueueEntry, String> {

    Page<QueueEntry> findByStatus(QueueStatus status, Pageable pageable);

    Page<QueueEntry> findBySourceId(String sourceId, Pageable pageable);

    Page<QueueEntry> findByStatusAndSourceId(QueueStatus status, String sourceId, Pageable pageable);

    long countByStatus(QueueStatus status);
}
