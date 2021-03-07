package org.java2uml.java2umlapi.lightWeight.repository;

import org.java2uml.java2umlapi.lightWeight.Field;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DisplayName("When using fieldRepository, ")
class FieldRepositoryTest {

    @Autowired
    FieldRepository fieldRepository;

    @Test
    @DisplayName("injected fieldRepository should not be null.")
    void fieldRepositoryIsNotNull() {
        assertThat(fieldRepository).isNotNull();
    }

    @Test
    @DisplayName("using findAllByName should return list containing all the instances which contains passed name.")
    void findAllByName() {
        var saved = getSavedField();
        var retrieved = fieldRepository.findAllByName("test").get(0);
        assertEquals(saved, retrieved, "saved instance should be same as retrieved instance");
    }

    @Test
    @DisplayName("using findAllByOwnerId should return list containing all the instances which contains passed ownerId.")
    void findAllByOwnerId() {
        var saved = getSavedField();
        saved.setOwnerId(1L);
        var retrieved = fieldRepository.findAllByOwnerId(1L).get(0);
        assertEquals(saved, retrieved, "saved instance should be same as retrieved instance");
    }

    @Test
    @DisplayName("using findAllByTypeName should return list containing all the instances which contains passed typeName.")
    void findAllByTypeName() {
        var saved = getSavedField();
        var retrieved = fieldRepository.findAllByTypeName("int").get(0);
        assertEquals(saved, retrieved, "saved instance should be same as retrieved instance");
    }

    private Field getSavedField() {
        return fieldRepository.save(new Field("int", "test", "private", false));
    }
}