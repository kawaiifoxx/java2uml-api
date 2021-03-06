package org.java2uml.java2umlapi.lightWeight.repository;

import org.java2uml.java2umlapi.lightWeight.Method;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DisplayName("When using methodRepository, ")
class MethodRepositoryTest {

    @Autowired
    MethodRepository methodRepository;

    @Test
    @DisplayName("injected method repository should not be null.")
    void methodRepositoryIsNotNull() {
        assertThat(methodRepository).isNotNull();
    }

    @Test
    @DisplayName("using findAllByName, should return a list containing" +
            " all the instances of method which contain passed name.")
    void findAllByName() {
        var saved = methodRepository.save(new Method("test", "Test.test()", "int","private"));
        var retrieved = methodRepository.findAllByName("test").get(0);
        assertEquals(saved, retrieved, "saved instanced should be same as retrieved instance.");
    }

    @Test
    @DisplayName("using findAllByName, should return a list containing" +
            " all the instances of method which contain passed ownerId.")
    void findAllByOwnerId() {
        var saved = methodRepository.save(new Method("test", "Test.test()", "int","private"));
        saved.setOwnerId(1L);
        var retrieved = methodRepository.findAllByOwnerId(1L).get(0);
        assertEquals(saved, retrieved, "saved instanced should be same as retrieved instance.");
    }

    @Test
    @DisplayName("using findAllByName, should return a list containing" +
            " all the instances of method which contain passed returnType.")
    void findAllByReturnType() {
        var saved = methodRepository.save(new Method("test", "Test.test()", "int","private"));
        var retrieved = methodRepository.findAllByReturnType("int").get(0);
        assertEquals(saved, retrieved, "saved instanced should be same as retrieved instance.");

    }
}