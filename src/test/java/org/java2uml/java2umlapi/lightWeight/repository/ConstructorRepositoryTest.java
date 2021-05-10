package org.java2uml.java2umlapi.lightWeight.repository;

import org.java2uml.java2umlapi.lightWeight.Body;
import org.java2uml.java2umlapi.lightWeight.ClassOrInterface;
import org.java2uml.java2umlapi.lightWeight.Constructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DisplayName("When using constructorRepository, ")
class ConstructorRepositoryTest {

    @Autowired
    ConstructorRepository constructorRepository;
    @Autowired
    ClassOrInterfaceRepository classOrInterfaceRepository;

    @Test
    @DisplayName("injected constructorRepository should not be null")
    void constructRepositoryIsNotNull() {
        assertThat(constructorRepository).isNotNull();
        assertThat(classOrInterfaceRepository).isNotNull();
    }

    @Test
    @DisplayName("using findConstructorByName, should return a list of constructor containing the given name.")
    void findConstructorByName() {
        var saved = constructorRepository.save(
                new Constructor("Test", "Test.Test()", "PUBLIC", new Body("{}")));
        var retrieved = constructorRepository.findConstructorByName("Test").get(0);
        assertEquals(saved, retrieved, "saved constructor instance should be same as retrieved instance.");
    }

    @Test
    @DisplayName("using findConstructorByParent, should return a list of constructor containing the given parentId.")
    void findConstructorByParent() {
        var classOrInterface = classOrInterfaceRepository.save(
                new ClassOrInterface.Builder().withName("Test").withIsClass(true).withIsExternal(false).build()
        );
        var saved = constructorRepository.save(
                new Constructor("Test", "Test.Test()", "PUBLIC", new Body("{}"))
        );
        saved.setParent(classOrInterface);
        classOrInterface.setClassConstructors(new ArrayList<>(List.of(saved)));
        classOrInterface = classOrInterfaceRepository.save(classOrInterface);
        var retrieved = constructorRepository.findConstructorByParent(classOrInterface).get(0);
        assertEquals(saved, retrieved, "saved constructor instance should be same as retrieved instance.");

    }
}