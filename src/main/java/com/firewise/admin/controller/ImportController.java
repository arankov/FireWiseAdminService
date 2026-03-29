package com.firewise.admin.controller;

import com.firewise.admin.dto.QueueEntryDto;
import com.firewise.admin.model.Source;
import com.firewise.admin.repository.SourceRepository;
import com.firewise.admin.service.ImportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/import")
public class ImportController {

    private final ImportService importService;
    private final SourceRepository sourceRepository;

    public ImportController(ImportService importService, SourceRepository sourceRepository) {
        this.importService = importService;
        this.sourceRepository = sourceRepository;
    }

    @PostMapping("/simulate")
    public ResponseEntity<List<QueueEntryDto>> simulate(@RequestBody Map<String, Object> body) {
        String sourceId = (String) body.get("sourceId");
        int count = body.containsKey("count") ? ((Number) body.get("count")).intValue() : 5;
        return ResponseEntity.ok(importService.simulateImport(sourceId, count));
    }

    @GetMapping("/sources")
    public List<Source> getSources() {
        return sourceRepository.findAll();
    }
}
