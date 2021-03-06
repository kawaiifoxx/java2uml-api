package org.java2uml.java2umlapi.lightWeight.repository;

import org.java2uml.java2umlapi.lightWeight.EnumConstant;
import org.java2uml.java2umlapi.lightWeight.EnumLW;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DisplayName("When using enumConstantRepository, ")
class EnumConstantRepositoryTest {
    @Autowired
    EnumConstantRepository enumConstantRepository;

    @Autowired
    EnumLWRepository enumLWRepository;

    @Test
    @DisplayName("injected enumConstantRepository should not be null.")
    void enumConstantRepositoryIsNotNull() {
        assertThat(enumConstantRepository).isNotNull();
    }

    @Test
    @DisplayName("injected enumLWRepository should not be null.")
    void enumLWRepositoryIsNotNull() {
        assertThat(enumLWRepository).isNotNull();
    }

    @Test
    @DisplayName("using findEnumConstantByEnumLW should return all enum constants contained in EnumLW")
    void findEnumConstantByEnumLW() {
        EnumLW enumLW = new EnumLW("EnumTest");
        var enumConstant1 = new EnumConstant("TEST1");
        var enumConstant2 = new EnumConstant("TEST2");
        var enumConstant3 = new EnumConstant("TEST3");
        var enumConstant4 = new EnumConstant("TEST4");

        enumConstant1.setEnumLW(enumLW);
        enumConstant2.setEnumLW(enumLW);
        enumConstant3.setEnumLW(enumLW);
        enumConstant4.setEnumLW(enumLW);

        var expected = Set.of(
                enumConstant1,
                enumConstant2,
                enumConstant3,
                enumConstant4
        );

        enumLW.setEnumConstants(new ArrayList<>(expected));

        enumLWRepository.save(enumLW);

        var actual = enumConstantRepository.findEnumConstantByEnumLW(enumLW);

        assertEquals(expected, new HashSet<>(actual), "stored enumConstants are not same as retrieved enumConstants.");
    }

    @Test
    @DisplayName("using findEnumConstantByName, should return all the enumConstants with same as passed name.")
    void findEnumConstantByName() {
        var name = "TEST";
        var enumConstant = new EnumConstant(name);
        enumConstantRepository.save(enumConstant);
        var retrieved = enumConstantRepository.findEnumConstantByName(name).get(0);
        assertEquals(enumConstant, retrieved, "stored enumConstant is not same as retrieved enumConstant");
    }
}