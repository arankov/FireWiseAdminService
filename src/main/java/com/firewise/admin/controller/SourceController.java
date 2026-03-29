package com.firewise.admin.controller;

import com.firewise.admin.model.Source;
import com.firewise.admin.repository.SourceRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sources")
public class SourceController {

    private final SourceRepository sourceRepository;

    public SourceController(SourceRepository sourceRepository) {
        this.sourceRepository = sourceRepository;
    }

    @GetMapping
    public List<Source> getAll() {
        return sourceRepository.findAll();
    }

    @GetMapping("/{id}")
    public Source getById(@PathVariable String id) {
        return sourceRepository.findById(id)
                .orElseThrow(() -> new com.firewise.admin.exception.ResourceNotFoundException("Source not found: " + id));
    }
}
