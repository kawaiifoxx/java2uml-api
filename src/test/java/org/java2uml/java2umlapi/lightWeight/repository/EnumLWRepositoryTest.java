package org.java2uml.java2umlapi.lightWeight.repository;

import org.java2uml.java2umlapi.lightWeight.EnumLW;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("When using enumLWRepository, ")
@DataJpaTest
class EnumLWRepositoryTest {

    @Autowired
    EnumLWRepository enumLWRepository;

    @Test
    @DisplayName("injected enumLWRepository should not be null.")
    void enumLWRepositoryIsNotNull() {
        assertThat(enumLWRepository).isNotNull();
    }

    @Test
    @DisplayName("using findAllByName should return a list of EnumLW have name same as passed name.")
    void findAllByName() {
        var saved = enumLWRepository.save(new EnumLW("Test"));
        var retrieved = enumLWRepository.findAllByName("Test").get(0);
        assertEquals(saved, retrieved, "saved instance of enumLWRepository should be same as retrieved instance");
    }

    @Test
    @DisplayName("using findAllByName should return a list of EnumLW have name same as passed name.")
    void findAllBySourceId() {
        var saved = enumLWRepository.save(new EnumLW("Test"));
        saved.setSourceId(1L);
        var retrieved = enumLWRepository.findAllBySourceId(1L).get(0);
        assertEquals(saved, retrieved, "saved instance of enumLWRepository should be same as retrieved instance");
    }

    @Test
    @DisplayName("using deleteEnumLWBySourceId should delete all instances of EnumLW having passed sourceId.")
    void deleteEnumLWBySourceId() {
        var saved = enumLWRepository.save(new EnumLW("Test"));
        saved.setSourceId(1L);
        enumLWRepository.deleteEnumLWBySourceId(1L);
        var retrievedSize = enumLWRepository.findAllBySourceId(1L).size();
        assertEquals(0, retrievedSize, "size of the retrieved list should be 0 after deletion.");

    }
}