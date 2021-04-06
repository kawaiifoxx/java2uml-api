package org.java2uml.java2umlapi.lightWeight.repository;

import org.java2uml.java2umlapi.lightWeight.Constructor;
import org.java2uml.java2umlapi.lightWeight.Method;
import org.java2uml.java2umlapi.lightWeight.Param;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DisplayName("When using paramRepository, ")
class ParamRepositoryTest {

    @Autowired
    private ParamRepository paramRepository;
    @Autowired
    private ConstructorRepository constructorRepository;
    @Autowired
    private MethodRepository methodRepository;

    @Test
    @DisplayName("injected repository should not be null.")
    void paramRepositoryIsNotNull() {
        assertThat(paramRepository).isNotNull();
        assertThat(constructorRepository).isNotNull();
        assertThat(methodRepository).isNotNull();
    }

    @Test
    @DisplayName("using findAllByName should return a list containing all " +
            "the instances of param which has same name as passed name.")
    void findAllByName() {
        var saved = paramRepository.save(new Param("int", "test"));
        var retrieved = paramRepository.findAllByName("test").get(0);
        assertEquals(saved, retrieved, "saved instance should be same as retrieved instance.");
    }

    @Test
    @DisplayName("using findAllByName should return a list containing all " +
            "the instances of param which has same typeName as passed typeName.")
    void findAllByTypeName() {
        var saved = paramRepository.save(new Param("int", "test"));
        var retrieved = paramRepository.findAllByTypeName("int").get(0);
        assertEquals(saved, retrieved, "saved instance should be same as retrieved instance.");
    }

    @Test
    @DisplayName("using findAllByParent should return a list containing all " +
            "the instances of param which has same parentId as passed parent.")
    void findAllByParent() {
        var constructor = new Constructor("Test", "Test()", "public", true);
        var saved = new Param("int", "test");
        constructor.setConstructorParameters(List.of(saved));
        saved.setParent(constructor);
        constructor = constructorRepository.save(constructor);
        var retrieved = paramRepository.findAllByParent(constructor).get(0);
        assertEquals(saved, retrieved, "saved instance should be same as retrieved instance.");
    }

    @Test
    @DisplayName("removing constructor should cascade to it's parameters.")
    void deletingConstructorShouldDeleteAllAssociatedParameters() {
        var constructor = constructorRepository.save(
                new Constructor("Test", "Test()", "public", true)
        );
        var saved = paramRepository.save(new Param("int", "test"));
        saved.setParent(constructor);
        constructor.setConstructorParameters(new ArrayList<>(List.of(saved)));
        constructor = constructorRepository.save(constructor);
        constructorRepository.delete(constructor);
        var retrievedSize = paramRepository.findAllByParent(constructor).size();

        assertThat(retrievedSize).describedAs("retrieved size should be 0 after deleting parent.")
                .isEqualTo(0);
    }

    @Test
    @DisplayName("removing method should cascade to it's parameters.")
    void deletingMethodShouldDeleteAllAssociatedParameters() {
        var method = methodRepository.save(
                new Method.Builder()
                        .withName("Test")
                        .withSignature("Test()")
                        .withReturnType("int")
                        .withVisibility("public")
                        .build()
        );
        var saved = paramRepository.save(new Param("int", "test"));
        saved.setParent(method);
        method.setMethodParameters(new ArrayList<>(List.of(saved)));
        method = methodRepository.save(method);
        methodRepository.delete(method);
        var retrievedSize = paramRepository.findAllByParent(method).size();

        assertThat(retrievedSize).describedAs("retrieved size should be 0 after deleting parent.")
                .isEqualTo(0);
    }
}