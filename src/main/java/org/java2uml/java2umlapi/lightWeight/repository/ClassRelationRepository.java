package org.java2uml.java2umlapi.lightWeight.repository;

import org.java2uml.java2umlapi.lightWeight.ClassOrInterface;
import org.java2uml.java2umlapi.lightWeight.ClassRelation;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ClassRelationRepository extends CrudRepository<ClassRelation, Long> {
    List<ClassRelation> findAllByFrom(ClassOrInterface from);
    List<ClassRelation> findAllByTo(ClassOrInterface to);
    void deleteAllByFrom(ClassOrInterface from);
    void deleteAllByTo(ClassOrInterface to);
}
