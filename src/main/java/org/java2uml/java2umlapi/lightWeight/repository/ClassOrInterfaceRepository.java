package org.java2uml.java2umlapi.lightWeight.repository;

import org.java2uml.java2umlapi.lightWeight.ClassOrInterface;
import org.java2uml.java2umlapi.lightWeight.LightWeight;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ClassOrInterfaceRepository extends CrudRepository<ClassOrInterface, Long> {
    List<ClassOrInterface> findAllByName(String name);
    List<ClassOrInterface> findAllByParent(LightWeight parent);
    void deleteAllByParent(LightWeight parent);
}
