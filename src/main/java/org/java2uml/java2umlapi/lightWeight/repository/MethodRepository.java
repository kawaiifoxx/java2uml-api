package org.java2uml.java2umlapi.lightWeight.repository;

import org.java2uml.java2umlapi.lightWeight.Method;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MethodRepository extends CrudRepository<Method, Long> {
    List<Method> findAllByName(String name);
    List<Method> findAllByOwnerId(Long OwnerId);
    List<Method> findAllByReturnType(String returnType);
}
