package org.java2uml.java2umlapi.lightWeight.repository;

import org.java2uml.java2umlapi.lightWeight.EnumConstant;
import org.java2uml.java2umlapi.lightWeight.LightWeight;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EnumConstantRepository extends CrudRepository<EnumConstant, Long> {
    List<EnumConstant> findAllByParent(LightWeight parent);
    List<EnumConstant> findEnumConstantByName(String name);
}