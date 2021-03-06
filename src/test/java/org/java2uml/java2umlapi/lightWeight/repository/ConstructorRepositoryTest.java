package org.java2uml.java2umlapi.lightWeight.repository;

import org.java2uml.java2umlapi.lightWeight.Body;
import org.java2uml.java2umlapi.lightWeight.Constructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DisplayName("When using constructorRepository, ")
class ConstructorRepositoryTest {

    @Autowired
    ConstructorRepository constructorRepository;

    @Test
    @DisplayName("injected constructorRepository should not be null")
    void constructRepositoryIsNotNull() {
        assertThat(constructorRepository).isNotNull();
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
    @DisplayName("using findConstructorByOwnerId, should return a list of constructor containing the given ownerId.")
    void findConstructorByOwnerId() {
        var saved = constructorRepository.save(
                new Constructor("Test", "Test.Test()", "PUBLIC", new Body("{}")));
        saved.setOwnerId(1L);
        var retrieved = constructorRepository.findConstructorByOwnerId(1L).get(0);
        assertEquals(saved, retrieved, "saved constructor instance should be same as retrieved instance.");

    }
}