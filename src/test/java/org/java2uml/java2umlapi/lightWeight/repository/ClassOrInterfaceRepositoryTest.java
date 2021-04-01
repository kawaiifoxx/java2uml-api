package org.java2uml.java2umlapi.lightWeight.repository;

import org.java2uml.java2umlapi.lightWeight.Body;
import org.java2uml.java2umlapi.lightWeight.ClassOrInterface;
import org.java2uml.java2umlapi.lightWeight.Source;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DisplayName("When using classOrInterfaceRepository, ")
class ClassOrInterfaceRepositoryTest {

    @Autowired
    ClassOrInterfaceRepository classOrInterfaceRepository;
    @Autowired
    SourceRepository sourceRepository;

    @Test
    @DisplayName("injected classOrInterfaceRepository should not be null.")
    void classOrInterfaceRepositoryIsNotNull() {
        assertThat(classOrInterfaceRepository).isNotNull();
        assertThat(sourceRepository).isNotNull();
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

    /**
     * @param source source to be assigned as a parent to classOrInterfaces.
     * @return classOrInterface list and saves all of this classOrInterfaces in the source.
     */
    private List<ClassOrInterface> getClassOrInterfaces(Source source) {
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
        class1.setParent(source);
        class2.setParent(source);
        class3.setParent(source);
        class4.setParent(source);
        class5.setParent(source);
        var classArr = new ArrayList<ClassOrInterface>();
        classArr.add(class1);
        classArr.add(class2);
        classArr.add(class3);
        classArr.add(class4);
        classArr.add(class5);
        source.setClassOrInterfaceList(classArr);
        return classArr;
    }

    @Test
    @DisplayName("using findAllByParent, should return all the classOrInterface associated with passed parent")
    void findAllByParent() {
        var source = sourceRepository.save(new Source());
        var saved = getClassOrInterfaces(source);
        var retrieved = classOrInterfaceRepository.findAllByParent(source);
        assertEquals(new HashSet<>(saved), new HashSet<>(retrieved),
                "all the classes with passed parent should be retrieved.");
    }

    @Test
    @DisplayName("using deleteClassOrInterfaceByParent, should remove all the " +
            "classOrInterface associated with passed parent")
    void deleteClassOrInterfaceByParent() {
        var source = sourceRepository.save(new Source());
        getClassOrInterfaces(source);
        source.getClassOrInterfaceList().clear();
        classOrInterfaceRepository.deleteAllByParent(source);

        var retrieved = classOrInterfaceRepository.findAllByParent(source);
        assertEquals(0, retrieved.size(), "all the classes with passed source should be deleted and " +
                "hence, size of retrieved should be zero.");
    }

    @Test
    @DisplayName("removing source, should remove all classOrInterfaces associated with it.")
    void deletingSourceShouldCascadeToClassOrInterface() {
        var source = sourceRepository.save(new Source());
        getClassOrInterfaces(source);
        sourceRepository.delete(source);
        var retrieved = classOrInterfaceRepository.findAllByParent(source);
        assertEquals(0, retrieved.size(),"all the classes with deleted source should be deleted and " +
                "hence, size of retrieved should be zero.");
    }
}