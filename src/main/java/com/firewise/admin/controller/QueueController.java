package com.firewise.admin.controller;

import com.firewise.admin.dto.QueueEntryDto;
import com.firewise.admin.model.QueueEntry;
import com.firewise.admin.model.QueueStatus;
import com.firewise.admin.service.ApprovalService;
import com.firewise.admin.service.QueueService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/queue")
public class QueueController {

    private final QueueService queueService;
    private final ApprovalService approvalService;

    public QueueController(QueueService queueService, ApprovalService approvalService) {
        this.queueService = queueService;
        this.approvalService = approvalService;
    }

    @GetMapping
    public Page<QueueEntryDto> getQueueEntries(
            @RequestParam(required = false) QueueStatus status,
            @RequestParam(required = false) String sourceId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return queueService.getQueueEntries(status, sourceId,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    @GetMapping("/{id}")
    public QueueEntryDto getQueueEntry(@PathVariable String id) {
        return queueService.getQueueEntry(id);
    }

    @PostMapping
    public ResponseEntity<QueueEntryDto> createQueueEntry(@RequestBody QueueEntry entry) {
        return ResponseEntity.ok(queueService.createQueueEntry(entry));
    }

    @PutMapping("/{id}")
    public ResponseEntity<QueueEntryDto> updateQueueEntry(@PathVariable String id, @RequestBody QueueEntry updates) {
        return ResponseEntity.ok(queueService.updateQueueEntry(id, updates));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<QueueEntryDto> changeStatus(@PathVariable String id, @RequestBody Map<String, String> body) {
        QueueStatus newStatus = QueueStatus.valueOf(body.get("status"));
        return ResponseEntity.ok(queueService.changeStatus(id, newStatus));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<QueueEntryDto> approve(@PathVariable String id, @RequestBody(required = false) Map<String, String> body) {
        String reviewedBy = body != null ? body.get("reviewedBy") : "admin";
        return ResponseEntity.ok(approvalService.approve(id, reviewedBy));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<QueueEntryDto> reject(@PathVariable String id, @RequestBody(required = false) Map<String, String> body) {
        String adminNotes = body != null ? body.get("adminNotes") : null;
        String reviewedBy = body != null ? body.get("reviewedBy") : "admin";
        return ResponseEntity.ok(approvalService.reject(id, adminNotes, reviewedBy));
    }
}
