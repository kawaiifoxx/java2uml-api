package org.java2uml.java2umlapi.lightWeight.repository;

import org.java2uml.java2umlapi.lightWeight.EnumConstant;
import org.java2uml.java2umlapi.lightWeight.EnumLW;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EnumConstantRepository extends CrudRepository<EnumConstant, Long> {
    List<EnumConstant> findEnumConstantByEnumLW(EnumLW enumLW);
    List<EnumConstant> findEnumConstantByName(String name);
}