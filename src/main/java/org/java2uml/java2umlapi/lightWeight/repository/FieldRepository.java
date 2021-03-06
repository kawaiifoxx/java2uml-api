package org.java2uml.java2umlapi.lightWeight.repository;

import org.java2uml.java2umlapi.lightWeight.Field;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FieldRepository  extends CrudRepository<Field, Long> {
    List<Field> findAllByName(String name);
    List<Field> findAllByOwnerId(Long ownerId);
    List<Field> findAllByTypeName(String typeName);
}
