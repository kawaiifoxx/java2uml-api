package org.java2uml.java2umlapi.lightWeight.repository;

import org.java2uml.java2umlapi.lightWeight.Body;
import org.java2uml.java2umlapi.lightWeight.ClassOrInterface;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DisplayName("When using classOrInterfaceRepository, ")
class ClassOrInterfaceRepositoryTest {

    @Autowired
    ClassOrInterfaceRepository classOrInterfaceRepository;

    @Test
    @DisplayName("injected classOrInterfaceRepository should not be null.")
    void classOrInterfaceRepositoryIsNotNull() {
        assertThat(classOrInterfaceRepository).isNotNull();
    }

    @Test
    @DisplayName("using findClassOrInterfaceByName, should return all classes with " +
            "same name as passed in findClassOrInterfaceByName.")
    void findAllByName() {
        var saved = new ClassOrInterface("TestClass", true, false, new Body("\n{\n}"));
        classOrInterfaceRepository.save(saved);
        var retrieved = classOrInterfaceRepository.findAllByName("TestClass").get(0);
        assertEquals(saved, retrieved
                , "Retrieved classOrInterface is should be same as saved classOrInterface");
    }

    @Test
    @DisplayName("using findAllBySourceId, should return all the classOrInterface associated with passed sourceId")
    void findAllBySourceId() {
        var class1 = classOrInterfaceRepository.save(
                new ClassOrInterface("T1", true, false, new Body("{}")));
        var class2 = classOrInterfaceRepository.save(
                new ClassOrInterface("T2", true, false, new Body("{}")));
        var class3 = classOrInterfaceRepository.save(
                new ClassOrInterface("T3", true, false, new Body("{}")));
        var class4 = classOrInterfaceRepository.save(
                new ClassOrInterface("T4", true, false, new Body("{}")));
        var class5 = classOrInterfaceRepository.save(
                new ClassOrInterface("T5", true, false, new Body("{}")));

        class1.setSourceId(1L);
        class2.setSourceId(1L);
        class3.setSourceId(1L);
        class4.setSourceId(1L);
        class5.setSourceId(1L);

        var setOfClasses = Set.of(
                class1,
                class2,
                class3,
                class4,
                class5
        );

        var retrieved = classOrInterfaceRepository.findAllBySourceId(1L);

        assertEquals(setOfClasses, new HashSet<>(retrieved),
                "all the classes with sourceId 1L should be retrieved.");
    }

    @Test
    void deleteClassOrInterfaceBySourceId() {
        var class1 = classOrInterfaceRepository.save(
                new ClassOrInterface("T1", true, false, new Body("{}")));
        var class2 = classOrInterfaceRepository.save(
                new ClassOrInterface("T2", true, false, new Body("{}")));
        var class3 = classOrInterfaceRepository.save(
                new ClassOrInterface("T3", true, false, new Body("{}")));
        var class4 = classOrInterfaceRepository.save(
                new ClassOrInterface("T4", true, false, new Body("{}")));
        var class5 = classOrInterfaceRepository.save(
                new ClassOrInterface("T5", true, false, new Body("{}")));

        class1.setSourceId(1L);
        class2.setSourceId(1L);
        class3.setSourceId(1L);
        class4.setSourceId(1L);
        class5.setSourceId(1L);

        classOrInterfaceRepository.deleteAllBySourceId(1L);
        var retrieved = classOrInterfaceRepository.findAllBySourceId(1L);

        assertEquals(0, retrieved.size(), "all the classes with sourceId 1L should be deleted and " +
                "hence, size of retrieved should be zero.");
    }
}