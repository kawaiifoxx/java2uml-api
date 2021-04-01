package org.java2uml.java2umlapi.lightWeight.repository;

import org.java2uml.java2umlapi.lightWeight.Field;
import org.java2uml.java2umlapi.lightWeight.LightWeight;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FieldRepository  extends CrudRepository<Field, Long> {
    List<Field> findAllByName(String name);
    List<Field> findAllByParent(LightWeight parent);
    List<Field> findAllByTypeName(String typeName);
}
