package org.java2uml.java2umlapi.lightWeight.repository;

import org.java2uml.java2umlapi.lightWeight.TypeParam;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DisplayName("When using typeParamRepository, ")
class TypeParamRepositoryTest {

    @Autowired
    TypeParamRepository typeParamRepository;

    @Test
    @DisplayName("injected typeParamRepository should not be null.")
    void typeParamRepositoryIsNotNull() {
        assertThat(typeParamRepository).isNotNull();
    }

    @Test
    @DisplayName("using findAllByOwnerId should return list containing " +
            "instances of typeParam which have same ownerId as passed ownerId.")
    void findAllByOwnerId() {
        var saved = typeParamRepository.save(new TypeParam("test"));
        saved.setOwnerId(1L);
        var retrieved = typeParamRepository.findAllByOwnerId(1L).get(0);
        assertEquals(saved, retrieved, "saved instance should be same as retrieved instance");
    }

    @Test
    @DisplayName("using findAllByOwnerId should return list containing " +
            "instances of typeParam which have same name as passed name.")
    void findAllByName() {
        var saved = typeParamRepository.save(new TypeParam("test"));
        var retrieved = typeParamRepository.findAllByName("test").get(0);
        assertEquals(saved, retrieved, "saved instance should be same as retrieved instance");
    }
}