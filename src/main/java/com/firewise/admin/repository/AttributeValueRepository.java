package com.firewise.admin.repository;

import com.firewise.admin.model.AttributeValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AttributeValueRepository extends JpaRepository<AttributeValue, String> {

    List<AttributeValue> findByPlantId(String plantId);

    List<AttributeValue> findByPlantIdAndAttributeId(String plantId, String attributeId);

    @Query("SELECT DISTINCT av.attributeId FROM AttributeValue av WHERE av.plantId = :plantId")
    List<String> findDistinctAttributeIdsByPlantId(@Param("plantId") String plantId);

    void deleteByPlantId(String plantId);
}
