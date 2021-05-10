package org.java2uml.java2umlapi.lightWeight.repository;

import org.java2uml.java2umlapi.lightWeight.ClassOrInterface;
import org.java2uml.java2umlapi.lightWeight.Constructor;
import org.java2uml.java2umlapi.lightWeight.Method;
import org.java2uml.java2umlapi.lightWeight.TypeParam;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DisplayName("When using typeParamRepository, ")
class TypeParamRepositoryTest {

    @Autowired
    TypeParamRepository typeParamRepository;
    @Autowired
    ClassOrInterfaceRepository classOrInterfaceRepository;
    @Autowired
    ConstructorRepository constructorRepository;
    @Autowired
    MethodRepository methodRepository;

    @Test
    @DisplayName("injected typeParamRepository should not be null.")
    void typeParamRepositoryIsNotNull() {
        assertThat(typeParamRepository).isNotNull();
        assertThat(classOrInterfaceRepository).isNotNull();
        assertThat(constructorRepository).isNotNull();
        assertThat(methodRepository).isNotNull();
    }

    @Test
    @DisplayName("using findAllByParent should return list containing " +
            "instances of typeParam which have same parent as passed parent.")
    void findAllByParent() {
        var method = new Method.Builder()
                .withName("test")
                .withSignature("test()")
                .withReturnType("int")
                .withVisibility("public")
                .build();

        var saved = new TypeParam("test");
        method.setMethodTypeParameters(List.of(saved));
        saved.setParent(method);
        method = methodRepository.save(method);
        var retrieved = typeParamRepository.findAllByParent(method).get(0);
        assertEquals(saved, retrieved, "saved instance should be same as retrieved instance");
    }

    @Test
    @DisplayName("using findAllByName should return list containing " +
            "instances of typeParam which have same name as passed name.")
    void findAllByName() {
        var saved = typeParamRepository.save(new TypeParam("test"));
        var retrieved = typeParamRepository.findAllByName("test").get(0);
        assertEquals(saved, retrieved, "saved instance should be same as retrieved instance");
    }

    @Test
    @DisplayName("removing parent should remove all the typeParameters which belong to this parent.")
    void deletingClassOrInterfaceShouldDeleteAllRelatedTypeParameters() {
        var classOrInterface = classOrInterfaceRepository.save(
                new ClassOrInterface.Builder().withName("Test").withIsClass(true).withIsExternal(false).build()
        );
        var saved = typeParamRepository.save(new TypeParam("test"));
        saved.setParent(classOrInterface);
        classOrInterface.setClassOrInterfaceTypeParameters(new ArrayList<>(List.of(saved)));
        classOrInterface = classOrInterfaceRepository.save(classOrInterface);
        classOrInterfaceRepository.delete(classOrInterface);
        var retrievedSize = typeParamRepository.findAllByParent(classOrInterface).size();
        assertThat(retrievedSize).describedAs("retrieved size should be zero after deleting parent.")
                .isEqualTo(0);
    }

    @Test
    @DisplayName("removing parent should remove all the typeParameters which belong to this parent.")
    void deletingConstructorsShouldDeleteAllRelatedTypeParameters() {
        var constructor = constructorRepository.save(
                new Constructor("Test", "Test()", "public", false)
        );
        var saved = typeParamRepository.save(new TypeParam("test"));
        saved.setParent(constructor);
        constructor.setConstructorTypeParameters(new ArrayList<>(List.of(saved)));
        constructor = constructorRepository.save(constructor);
        constructorRepository.delete(constructor);
        var retrievedSize = typeParamRepository.findAllByParent(constructor).size();
        assertThat(retrievedSize).describedAs("retrieved size should be zero after deleting parent.")
                .isEqualTo(0);
    }

    @Test
    @DisplayName("removing parent should remove all the typeParameters which belong to this parent.")
    void deletingMethodShouldDeleteAllRelatedTypeParameters() {
        var method = methodRepository.save(
                new Method.Builder()
                        .withName("test")
                        .withSignature("Test.test()")
                        .withReturnType("int")
                        .withVisibility("public")
                        .build()
        );
        var saved = typeParamRepository.save(new TypeParam("test"));
        saved.setParent(method);
        method.setMethodTypeParameters(new ArrayList<>(List.of(saved)));
        method = methodRepository.save(method);
        methodRepository.delete(method);
        var retrievedSize = typeParamRepository.findAllByParent(method).size();
        assertThat(retrievedSize).describedAs("retrieved size should be zero after deleting parent.")
                .isEqualTo(0);
    }
}