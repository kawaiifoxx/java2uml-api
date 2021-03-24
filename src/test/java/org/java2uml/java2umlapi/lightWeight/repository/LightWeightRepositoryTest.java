package org.java2uml.java2umlapi.lightWeight.repository;

import org.java2uml.java2umlapi.lightWeight.ClassOrInterface;
import org.java2uml.java2umlapi.lightWeight.LightWeight;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@DisplayName("When using lightWeightRepository, ")
@DataJpaTest
class LightWeightRepositoryTest {
    @Autowired
    LightWeightRepository lightWeightRepository;

    @Test
    @DisplayName("light weight repository should not be null.")
    void lightWeightRepositoryShouldNotBeNull() {
        assertThat(lightWeightRepository).isNotNull();
    }

    @Test
    @DisplayName("findById should return same lightWeight which was persisted.")
    void findLightWeightById() {
        LightWeight saved = lightWeightRepository.save(
                new ClassOrInterface("Test", true, false)
        );
        var retrieved = lightWeightRepository.findById(saved.getId());
        if (retrieved.isEmpty()) {
            fail("Persisted light weight should retrieved.");
            return;
        }
        assertThat(saved)
                .describedAs("saved light weight should be same as retrieved light weight ")
                .isEqualTo(retrieved.get());
    }
}