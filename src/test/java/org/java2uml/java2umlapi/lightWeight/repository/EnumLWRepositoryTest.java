package org.java2uml.java2umlapi.lightWeight.repository;

import org.java2uml.java2umlapi.lightWeight.EnumLW;
import org.java2uml.java2umlapi.lightWeight.Source;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("When using enumLWRepository, ")
@DataJpaTest
class EnumLWRepositoryTest {

    @Autowired
    EnumLWRepository enumLWRepository;
    @Autowired
    SourceRepository sourceRepository;

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
    void findAllByParent() {
        var source = new Source();
        var saved = enumLWRepository.save(new EnumLW("Test"));
        source.setEnumLWList(List.of(saved));
        saved.setParent(source);
        source = sourceRepository.save(source);
        var retrieved = enumLWRepository.findAllByParent(source).get(0);
        assertEquals(saved, retrieved, "saved instance of enumLWRepository should be same as retrieved instance");
    }

    @Test
    @DisplayName("using deleteEnumLWBySource should delete all instances of EnumLW having passed source.")
    void deleteEnumLWBySource() {
        var saved = enumLWRepository.save(new EnumLW("Test"));
        var source = new Source();
        source.setEnumLWList(new ArrayList<>(List.of(saved)));
        saved.setParent(source);
        source = sourceRepository.save(source);
        source.getEnumLWList().clear();
        enumLWRepository.deleteAllByParent(source);
        var retrievedSize = enumLWRepository.findAllByParent(source).size();
        assertEquals(0, retrievedSize, "size of the retrieved list should be 0 after deletion.");
    }

    @Test
    @DisplayName("removing source should remove delete all instances of enumLW having source as parent.")
    void deletingSourceShouldDeleteItsEnumLWList() {
        var saved = enumLWRepository.save(new EnumLW("Test"));
        var source = new Source();
        source.setEnumLWList(new ArrayList<>(List.of(saved)));
        saved.setParent(source);
        source = sourceRepository.save(source);
        sourceRepository.delete(source);
        var retrievedSize = enumLWRepository.findAllByParent(source).size();
        assertEquals(0, retrievedSize, "size of the retrieved list should be 0 after deletion.");
    }
}