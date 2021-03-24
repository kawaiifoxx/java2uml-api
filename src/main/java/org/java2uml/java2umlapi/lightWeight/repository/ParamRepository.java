package org.java2uml.java2umlapi.lightWeight.repository;

import org.java2uml.java2umlapi.lightWeight.LightWeight;
import org.java2uml.java2umlapi.lightWeight.Param;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ParamRepository extends CrudRepository<Param, Long> {
    List<Param> findAllByName(String name);
    List<Param> findAllByTypeName(String typeName);
    List<Param> findAllByParent(LightWeight parent);
}
