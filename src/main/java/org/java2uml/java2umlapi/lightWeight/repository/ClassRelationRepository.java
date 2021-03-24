package org.java2uml.java2umlapi.lightWeight.repository;

import org.java2uml.java2umlapi.lightWeight.ClassRelation;
import org.java2uml.java2umlapi.lightWeight.LightWeight;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ClassRelationRepository extends CrudRepository<ClassRelation, Long> {
    List<ClassRelation> findAllByFrom(LightWeight from);
    List<ClassRelation> findAllByTo(LightWeight to);
    void deleteAllByFrom(LightWeight from);
    void deleteAllByTo(LightWeight to);
}
