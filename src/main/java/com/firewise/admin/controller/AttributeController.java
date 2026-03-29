package com.firewise.admin.controller;

import com.firewise.admin.dto.AttributeTreeDto;
import com.firewise.admin.model.Attribute;
import com.firewise.admin.repository.AttributeRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/attributes")
public class AttributeController {

    private final AttributeRepository attributeRepository;

    public AttributeController(AttributeRepository attributeRepository) {
        this.attributeRepository = attributeRepository;
    }

    @GetMapping
    public List<Attribute> getAll() {
        return attributeRepository.findAll();
    }

    @GetMapping("/tree")
    public List<AttributeTreeDto> getTree() {
        List<Attribute> all = attributeRepository.findAll();
        Map<String, List<Attribute>> childrenByParent = all.stream()
                .filter(a -> a.getParentAttributeId() != null)
                .collect(Collectors.groupingBy(Attribute::getParentAttributeId));

        return all.stream()
                .filter(a -> a.getParentAttributeId() == null)
                .sorted(Comparator.comparing(Attribute::getName))
                .map(parent -> {
                    AttributeTreeDto dto = toTreeDto(parent);
                    List<Attribute> children = childrenByParent.getOrDefault(parent.getId(), List.of());
                    dto.setChildren(children.stream()
                            .sorted(Comparator.comparing(Attribute::getName))
                            .map(this::toTreeDto)
                            .toList());
                    return dto;
                })
                .toList();
    }

    private AttributeTreeDto toTreeDto(Attribute attr) {
        AttributeTreeDto dto = new AttributeTreeDto();
        dto.setId(attr.getId());
        dto.setName(attr.getName());
        dto.setValueType(attr.getValueType());
        dto.setSelectionType(attr.getSelectionType());
        dto.setValuesAllowed(attr.getValuesAllowed());
        return dto;
    }
}
