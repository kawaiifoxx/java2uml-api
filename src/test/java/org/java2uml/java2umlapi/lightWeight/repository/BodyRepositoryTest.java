package org.java2uml.java2umlapi.lightWeight.repository;

import org.java2uml.java2umlapi.lightWeight.Body;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DisplayName("When using bodyRepository, ")
class BodyRepositoryTest {

    @Autowired
    BodyRepository bodyRepository;

    @Test
    @DisplayName("injected userRepository should not be null.")
    void injectedComponentIsNotNull() {
        assertThat(bodyRepository).isNotNull();
    }

    @Test
    @DisplayName("upon retrieving Body, which contains \"test\" all persisted entities with \"test\" " +
            "in there content should be retrieved. ")
    void retrieveBodies() {
        var persisted = new Body("void test() {\n" +
                "        System.out.println(\"Hello This is a test for body!\");\n" +
                "    }");
        bodyRepository.save(persisted);
        var retrieved = bodyRepository.findAllByContentContains("test").get(0);
        assertEquals(persisted.getContent(), retrieved.getContent(), "Persisted and retrieved entities are not the same.");
    }

    @Test
    void findBodyByOwnerID() {
        var saved = new Body("{\nTestBody\n}");
        saved.setOwnerID(1L);
        bodyRepository.save(saved);
        var retrieved = bodyRepository.findBodyByOwnerID(1L);
        assertEquals(saved, retrieved, "Saved and retrieved body should be same.");
    }
}