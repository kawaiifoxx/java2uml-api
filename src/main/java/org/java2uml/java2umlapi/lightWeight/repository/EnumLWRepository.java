package org.java2uml.java2umlapi.lightWeight.repository;

import org.java2uml.java2umlapi.lightWeight.EnumLW;
import org.java2uml.java2umlapi.lightWeight.LightWeight;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EnumLWRepository extends CrudRepository<EnumLW, Long> {
    List<EnumLW> findAllByName(String name);

    List<EnumLW> findAllByParent(LightWeight parent);

    void deleteAllByParent(LightWeight parent);
}
