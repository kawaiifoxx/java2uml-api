package org.java2uml.java2umlapi.lightWeight.repository;

import org.java2uml.java2umlapi.lightWeight.Body;
import org.java2uml.java2umlapi.lightWeight.LightWeight;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface BodyRepository extends CrudRepository<Body, Long> {
    List<Body> findAllByContentContains(String text);
    Optional<Body> findByParent(LightWeight parent);
}
