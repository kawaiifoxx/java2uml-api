package org.java2uml.java2umlapi.lightWeight.repository;

import org.java2uml.java2umlapi.lightWeight.EnumLW;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EnumLWRepository extends CrudRepository<EnumLW, Long> {
    List<EnumLW> findAllByName(String name);

    List<EnumLW> findAllBySourceId(Long sourceId);

    void deleteEnumLWBySourceId(Long sourceId);
}
