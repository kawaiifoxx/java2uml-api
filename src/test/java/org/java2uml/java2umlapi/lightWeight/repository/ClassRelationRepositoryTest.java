package org.java2uml.java2umlapi.lightWeight.repository;

import org.java2uml.java2umlapi.lightWeight.Body;
import org.java2uml.java2umlapi.lightWeight.ClassOrInterface;
import org.java2uml.java2umlapi.lightWeight.ClassRelation;
import org.java2uml.java2umlapi.util.umlSymbols.RelationsSymbol;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DisplayName("When using classRelationRepository, ")
class ClassRelationRepositoryTest {

    @Autowired
    ClassRelationRepository classRelationRepository;

    @Autowired
    ClassOrInterfaceRepository classOrInterfaceRepository;

    @Test
    @DisplayName("injected classRelationRepository should not be null.")
    void classRelationRepositoryIsNotNull() {
        assertThat(classOrInterfaceRepository).isNotNull();
        assertThat(classRelationRepository).isNotNull();
    }

    @Test
    @DisplayName("using findAllByFrom, should retrieve all the ClassRelation with from.")
    void findAllByFrom() {
        ClassOrInterface from = getClassOrInterface("TestClass");
        ClassOrInterface to = getClassOrInterface("TestClass1");
        var classRelation = classRelationRepository.save(
                new ClassRelation(from, to, RelationsSymbol.AGGREGATION, null));
        var retrieved = classRelationRepository.findAllByFrom(from).get(0);

        assertEquals(classRelation, retrieved, "saved classRelation should be same as retrieved classRelation");
    }

    /**
     * @param name name of {@link ClassOrInterface}
     * @return An instance of {@link ClassOrInterface} with passed name.
     */
    private ClassOrInterface getClassOrInterface(String name) {
        return classOrInterfaceRepository.save(
                new ClassOrInterface
                        .Builder().withName(name)
                        .withIsClass(true).withIsExternal(false).withBody(new Body("{}")).build()
        );
    }

    @Test
    @DisplayName("using findAllByTo, should retrieve all the ClassRelation with to.")
    void findAllByTo() {
        ClassOrInterface from = getClassOrInterface("TestClass");
        ClassOrInterface to = getClassOrInterface("TestClass1");
        var classRelation = classRelationRepository.save(
                new ClassRelation(from, to, RelationsSymbol.AGGREGATION, null));
        var retrieved = classRelationRepository.findAllByTo(to).get(0);

        assertEquals(classRelation, retrieved, "saved classRelation should be same as retrieved classRelation");
    }

    @Test
    @DisplayName("using deleteAllByFrom, should remove all the " +
            "instances of classRelation containing given class in from field.")
    void deleteAllByFrom() {
        ClassOrInterface from = getClassOrInterface("TestClass");
        ClassOrInterface to = getClassOrInterface("TestClass1");
        classRelationRepository.save(new ClassRelation(from, to, RelationsSymbol.AGGREGATION, null));
        classRelationRepository.deleteAllByFrom(from);

        assertEquals(0, classRelationRepository.findAllByFrom(from).size(),
                "saved classRelation should be same as retrieved classRelation");
    }

    @Test
    @DisplayName("using deleteAllByTo, should remove all the " +
            "instances of classRelation containing given class in to field.")
    void deleteAllByTo() {
        ClassOrInterface from = getClassOrInterface("TestClass");
        ClassOrInterface to = getClassOrInterface("TestClass1");
        classRelationRepository.save(new ClassRelation(from, to, RelationsSymbol.AGGREGATION, null));
        classRelationRepository.deleteAllByTo(to);

        assertEquals(0, classRelationRepository.findAllByFrom(from).size(),
                "saved classRelation should be same as retrieved classRelation");

    }
}