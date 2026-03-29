package com.firewise.admin.repository;

import com.firewise.admin.model.Attribute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttributeRepository extends JpaRepository<Attribute, String> {

    List<Attribute> findByParentAttributeIdIsNull();

    List<Attribute> findByParentAttributeId(String parentAttributeId);
}
