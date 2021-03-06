package org.java2uml.java2umlapi.lightWeight.repository;

import org.java2uml.java2umlapi.lightWeight.Param;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DisplayName("When using paramRepository, ")
class ParamRepositoryTest {

    @Autowired
    private ParamRepository paramRepository;

    @Test
    @DisplayName("injected repository should not be null.")
    void paramRepositoryIsNotNull() {
        assertThat(paramRepository).isNotNull();
    }

    @Test
    @DisplayName("using findAllByName should return a list containing all " +
            "the instances of param which has same name as passed name.")
    void findAllByName() {
        var saved = paramRepository.save(new Param("int", "test"));
        var retrieved = paramRepository.findAllByName("test").get(0);
        assertEquals(saved, retrieved, "saved instance should be same as retrieved instance.");
    }

    @Test
    @DisplayName("using findAllByName should return a list containing all " +
            "the instances of param which has same typeName as passed typeName.")
    void findAllByTypeName() {
        var saved = paramRepository.save(new Param("int", "test"));
        var retrieved = paramRepository.findAllByTypeName("int").get(0);
        assertEquals(saved, retrieved, "saved instance should be same as retrieved instance.");
    }

    @Test
    @DisplayName("using findAllByName should return a list containing all " +
            "the instances of param which has same ownerId as passed ownerId.")
    void findAllByOwnerId() {
        var saved = paramRepository.save(new Param("int", "test"));
        saved.setOwnerId(1L);
        var retrieved = paramRepository.findAllByOwnerId(1L).get(0);
        assertEquals(saved, retrieved, "saved instance should be same as retrieved instance.");
    }
}