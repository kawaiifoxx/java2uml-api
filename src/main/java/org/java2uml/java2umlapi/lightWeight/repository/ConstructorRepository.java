package org.java2uml.java2umlapi.lightWeight.repository;

import org.java2uml.java2umlapi.lightWeight.Constructor;
import org.java2uml.java2umlapi.lightWeight.LightWeight;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ConstructorRepository extends CrudRepository<Constructor, Long> {
    List<Constructor> findConstructorByName(String name);
    List<Constructor> findConstructorByParent(LightWeight parent);
}
