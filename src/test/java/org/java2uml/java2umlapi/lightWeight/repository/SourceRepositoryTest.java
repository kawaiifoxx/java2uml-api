package org.java2uml.java2umlapi.lightWeight.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("When using sourceRepository, ")
class SourceRepositoryTest {
    @Autowired
    SourceRepository sourceRepository;

    @Test
    void sourceRepositoryIsNotNull() {
        assertThat(sourceRepository).isNotNull();
    }
}