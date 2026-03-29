package com.firewise.admin.controller;

import com.firewise.admin.dto.ClaudeSuggestionDto;
import com.firewise.admin.exception.ResourceNotFoundException;
import com.firewise.admin.model.QueueEntry;
import com.firewise.admin.repository.QueueEntryRepository;
import com.firewise.admin.service.ApiKeyCacheService;
import com.firewise.admin.service.ClaudeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ClaudeController {

    private final ApiKeyCacheService apiKeyCacheService;
    private final ClaudeService claudeService;
    private final QueueEntryRepository queueEntryRepository;

    public ClaudeController(ApiKeyCacheService apiKeyCacheService,
                            ClaudeService claudeService,
                            QueueEntryRepository queueEntryRepository) {
        this.apiKeyCacheService = apiKeyCacheService;
        this.claudeService = claudeService;
        this.queueEntryRepository = queueEntryRepository;
    }

    @PostMapping("/claude/api-key")
    public ResponseEntity<Map<String, String>> storeApiKey(@RequestBody Map<String, String> body) {
        String sessionId = body.get("sessionId");
        String apiKey = body.get("apiKey");
        if (sessionId == null || apiKey == null || apiKey.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "sessionId and apiKey are required"));
        }
        apiKeyCacheService.storeKey(sessionId, apiKey);
        return ResponseEntity.ok(Map.of("status", "stored"));
    }

    @DeleteMapping("/claude/api-key")
    public ResponseEntity<Map<String, String>> clearApiKey(@RequestParam String sessionId) {
        apiKeyCacheService.removeKey(sessionId);
        return ResponseEntity.ok(Map.of("status", "cleared"));
    }

    @GetMapping("/claude/api-key/status")
    public ResponseEntity<Map<String, Boolean>> getKeyStatus(@RequestParam String sessionId) {
        boolean valid = apiKeyCacheService.hasValidKey(sessionId);
        return ResponseEntity.ok(Map.of("valid", valid));
    }

    @PostMapping("/queue/{id}/ask-claude")
    public ResponseEntity<?> askClaude(@PathVariable String id, @RequestBody Map<String, String> body) {
        String sessionId = body.get("sessionId");
        if (sessionId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "sessionId is required"));
        }

        String apiKey = apiKeyCacheService.getKey(sessionId);
        if (apiKey == null) {
            return ResponseEntity.status(401).body(Map.of("error", "No valid API key. Please provide your Claude API key."));
        }

        QueueEntry entry = queueEntryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Queue entry not found: " + id));

        try {
            List<ClaudeSuggestionDto> suggestions = claudeService.askClaude(apiKey, entry);
            return ResponseEntity.ok(suggestions);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
