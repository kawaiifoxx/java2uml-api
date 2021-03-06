package org.java2uml.java2umlapi.lightWeight.repository;

import org.java2uml.java2umlapi.lightWeight.TypeParam;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TypeParamRepository extends CrudRepository<TypeParam, Long> {
    List<TypeParam> findAllByOwnerId(Long ownerId);
    List<TypeParam> findAllByName(String name);
}
