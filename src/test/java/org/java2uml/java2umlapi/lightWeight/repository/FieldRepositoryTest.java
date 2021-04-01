package org.java2uml.java2umlapi.lightWeight.repository;

import org.java2uml.java2umlapi.lightWeight.ClassOrInterface;
import org.java2uml.java2umlapi.lightWeight.EnumLW;
import org.java2uml.java2umlapi.lightWeight.Field;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DisplayName("When using fieldRepository, ")
class FieldRepositoryTest {

    @Autowired
    FieldRepository fieldRepository;
    @Autowired
    ClassOrInterfaceRepository classOrInterfaceRepository;
    @Autowired
    EnumLWRepository enumLWRepository;

    @Test
    @DisplayName("injected fieldRepository should not be null.")
    void fieldRepositoryIsNotNull() {
        assertThat(fieldRepository).isNotNull();
        assertThat(classOrInterfaceRepository).isNotNull();
    }

    @Test
    @DisplayName("using findAllByName should return list containing all the instances which contains passed name.")
    void findAllByName() {
        var saved = getSavedField();
        var retrieved = fieldRepository.findAllByName("test").get(0);
        assertEquals(saved, retrieved, "saved instance should be same as retrieved instance");
    }

    @Test
    @DisplayName("using findAllByParent should return list containing all the instances which contains passed parent.")
    void findAllByParent() {
        var saved = getSavedField();
        var classOrInterface = new ClassOrInterface("Test", true, false);
        classOrInterface.setClassFields(List.of(saved));
        saved.setParent(classOrInterface);
        classOrInterfaceRepository.save(classOrInterface);
        var retrieved = fieldRepository.findAllByParent(classOrInterface).get(0);
        assertEquals(saved, retrieved, "saved instance should be same as retrieved instance");
    }

    @Test
    @DisplayName("using findAllByTypeName should return list containing all " +
            "the instances which contains passed typeName.")
    void findAllByTypeName() {
        var saved = getSavedField();
        var retrieved = fieldRepository.findAllByTypeName("int").get(0);
        assertEquals(saved, retrieved, "saved instance should be same as retrieved instance");
    }

    @Test
    @DisplayName("removing classOrInterface should remove all fields which belong to deleted classOrInterface.")
    void deletingClassOrInterfaceShouldDeleteField() {
        var classOrInterface = classOrInterfaceRepository.save(
                new ClassOrInterface("Test", true, false)
        );
        var saved = getSavedField();
        classOrInterface.setClassFields(new ArrayList<>(List.of(saved)));
        classOrInterface = classOrInterfaceRepository.save(classOrInterface);
        classOrInterfaceRepository.delete(classOrInterface);
        assertThat(fieldRepository.findAllByParent(classOrInterface).size())
                .describedAs("retrieved size should be equal to zero.").isEqualTo(0);
    }

    @Test
    @DisplayName("removing enumLW should remove all fields which belong to deleted enumLW.")
    void deletingEnumLWShouldDeleteField() {
        var enumLW = enumLWRepository.save(
                new EnumLW("Test")
        );
        var saved = getSavedField();
        enumLW.setEnumFields(new ArrayList<>(List.of(saved)));
        enumLW = enumLWRepository.save(enumLW);
        enumLWRepository.delete(enumLW);
        assertThat(fieldRepository.findAllByParent(enumLW).size())
                .describedAs("retrieved size should be equal to zero.").isEqualTo(0);
    }

    private Field getSavedField() {
        return fieldRepository.save(new Field("int", "test", "private", false));
    }
}