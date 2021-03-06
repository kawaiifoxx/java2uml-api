package org.java2uml.java2umlapi.lightWeight.repository;

import org.java2uml.java2umlapi.lightWeight.Body;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BodyRepository extends CrudRepository<Body, Long> {
    List<Body> findAllByContentContains(String text);
    Body findBodyByOwnerID(Long ownerID);
}
