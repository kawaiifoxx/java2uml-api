package org.java2uml.java2umlapi.lightWeight.repository;

import org.java2uml.java2umlapi.lightWeight.ClassOrInterface;
import org.java2uml.java2umlapi.lightWeight.EnumLW;
import org.java2uml.java2umlapi.lightWeight.Method;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DisplayName("When using methodRepository, ")
class MethodRepositoryTest {

    @Autowired
    MethodRepository methodRepository;
    @Autowired
    ClassOrInterfaceRepository classOrInterfaceRepository;
    @Autowired
    EnumLWRepository enumLWRepository;

    @Test
    @DisplayName("injected method repository should not be null.")
    void methodRepositoryIsNotNull() {
        assertThat(methodRepository).isNotNull();
        assertThat(classOrInterfaceRepository).isNotNull();
        assertThat(enumLWRepository).isNotNull();
    }

    @Test
    @DisplayName("using findAllByName, should return a list containing" +
            " all the instances of method which contain passed name.")
    void findAllByName() {
        var saved = methodRepository.save(
                new Method.Builder()
                        .withName("test")
                        .withSignature("Test.test()")
                        .withReturnType("int")
                        .withVisibility("private")
                        .build()
        );
        var retrieved = methodRepository.findAllByName("test").get(0);
        assertEquals(saved, retrieved, "saved instanced should be same as retrieved instance.");
    }

    @Test
    @DisplayName("using findAllByParent, should return a list containing" +
            " all the instances of method which contain reference of passed parent.")
    void findAllByParent() {
        var saved = methodRepository.save(
                new Method.Builder()
                        .withName("test")
                        .withSignature("Test.test()")
                        .withReturnType("int")
                        .withVisibility("private")
                        .build()
        );
        ClassOrInterface classOrInterface = classOrInterfaceRepository.save(
                new ClassOrInterface.Builder().withName("Test").withIsClass(true).withIsExternal(false).build()
        );
        saved.setParent(classOrInterface);
        classOrInterface.setClassOrInterfaceMethods(new ArrayList<>(List.of(saved)));
        classOrInterface = classOrInterfaceRepository.save(classOrInterface);
        var retrieved = methodRepository.findAllByParent(classOrInterface).get(0);
        assertEquals(saved, retrieved, "saved instanced should be same as retrieved instance.");
    }

    @Test
    @DisplayName("using findAllByName, should return a list containing" +
            " all the instances of method which contain passed returnType.")
    void findAllByReturnType() {
        var saved = methodRepository.save(
                new Method.Builder()
                        .withName("test")
                        .withSignature("Test.test()")
                        .withReturnType("int")
                        .withVisibility("private")
                        .build()
        );
        var retrieved = methodRepository.findAllByReturnType("int").get(0);
        assertEquals(saved, retrieved, "saved instanced should be same as retrieved instance.");

    }

    @Test
    @DisplayName("deleting classOrInterface should to cascade to all of it's methods.")
    void deletingClassOrInterfaceShouldDeleteAllAssociatedMethods() {
        var saved = methodRepository.save(
                new Method.Builder()
                        .withName("test")
                        .withSignature("Test.test()")
                        .withReturnType("int")
                        .withVisibility("private")
                        .build()
        );
        ClassOrInterface classOrInterface = classOrInterfaceRepository.save(
                new ClassOrInterface.Builder().withName("Test").withIsClass(true).withIsExternal(false).build()
        );
        saved.setParent(classOrInterface);
        classOrInterface.setClassOrInterfaceMethods(new ArrayList<>(List.of(saved)));
        classOrInterface = classOrInterfaceRepository.save(classOrInterface);
        classOrInterfaceRepository.delete(classOrInterface);
        var retrieved = methodRepository.findAllByParent(classOrInterface);
        assertThat(retrieved.size()).describedAs("retrieved size should be 0 after deleting parent.")
                .isEqualTo(0);
    }

    @Test
    @DisplayName("deleting enumLW should to cascade to all of it's methods.")
    void deletingEnumLWShouldDeleteAllAssociatedMethods() {
        var saved = methodRepository.save(
                new Method.Builder()
                        .withName("test")
                        .withSignature("Test.test()")
                        .withReturnType("int")
                        .withVisibility("private")
                        .build()
        );
        EnumLW enumLW = enumLWRepository.save(
                new EnumLW("Test")
        );
        saved.setParent(enumLW);
        enumLW.setEnumMethods(new ArrayList<>(List.of(saved)));
        enumLW = enumLWRepository.save(enumLW);
        enumLWRepository.delete(enumLW);
        var retrieved = methodRepository.findAllByParent(enumLW);
        assertThat(retrieved.size()).describedAs("retrieved size should be 0 after deleting parent.")
                .isEqualTo(0);
    }
}