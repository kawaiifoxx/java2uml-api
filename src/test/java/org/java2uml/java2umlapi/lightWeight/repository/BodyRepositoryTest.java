package org.java2uml.java2umlapi.lightWeight.repository;

import org.java2uml.java2umlapi.lightWeight.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@DataJpaTest
@DisplayName("When using bodyRepository, ")
class BodyRepositoryTest {

    @Autowired
    BodyRepository bodyRepository;
    @Autowired
    ClassOrInterfaceRepository classOrInterfaceRepository;
    @Autowired
    EnumLWRepository enumLWRepository;
    @Autowired
    ConstructorRepository constructorRepository;
    @Autowired
    MethodRepository methodRepository;

    @Test
    @DisplayName("injected userRepository should not be null.")
    void injectedComponentIsNotNull() {
        assertThat(bodyRepository).isNotNull();
        assertThat(classOrInterfaceRepository).isNotNull();
        assertThat(enumLWRepository).isNotNull();
        assertThat(constructorRepository).isNotNull();
        assertThat(methodRepository).isNotNull();
    }

    @Test
    @DisplayName("upon retrieving Body, which contains \"test\" all persisted entities with \"test\" " +
            "in there content should be retrieved. ")
    void retrieveBodies() {
        var persisted = new Body("void test() {\n" +
                "        System.out.println(\"Hello This is a test for body!\");\n" +
                "    }");
        bodyRepository.save(persisted);
        var retrieved = bodyRepository.findAllByContentContains("test").get(0);
        assertEquals(persisted.getContent(), retrieved.getContent(), "Persisted and retrieved entities are not the same.");
    }

    @Test
    @DisplayName("using findByParentId should return body with given parent id.")
    void findBodyByParent() {
        var saved = bodyRepository.save(new Body("{\nTestBody\n}"));
        var parent = constructorRepository.save(
                new Constructor("Test", "Test()", "PUBLIC", saved)
        );
        saved.setParent(parent);
        parent.setBody(saved);
        parent = constructorRepository.save(parent);
        var retrieved = bodyRepository.findByParent(parent);
        if (retrieved.isEmpty()) {
            fail("Body Not Found!");
        }
        assertEquals(saved, retrieved.get(), "Saved and retrieved body should be same.");
    }

    @Test
    @DisplayName("deleting parent of body should delete the body.")
    void deletingClassOrInterfaceShouldDeleteItsBody() {
        var classOrInterface = classOrInterfaceRepository.save(
                new ClassOrInterface("Test", true, false)
        );
        var body = bodyRepository.save(new Body("test{\n}"));
        body.setParent(classOrInterface);
        classOrInterface.setBody(body);
        classOrInterface = classOrInterfaceRepository.save(classOrInterface);
        classOrInterfaceRepository.delete(classOrInterface);
        var retrieved = bodyRepository.findByParent(classOrInterface);
        assertThat(retrieved).describedAs("Body should not be present").isEmpty();
    }

    @Test
    @DisplayName("deleting parent of body should delete the body.")
    void deletingEnumLWShouldDeleteItsBody() {
        var enumLW = enumLWRepository.save(
                new EnumLW("Test")
        );
        var body = bodyRepository.save(new Body("test{\n}"));
        body.setParent(enumLW);
        enumLW.setBody(body);
        enumLW = enumLWRepository.save(enumLW);
        enumLWRepository.delete(enumLW);
        var retrieved = bodyRepository.findByParent(enumLW);
        assertThat(retrieved).describedAs("Body should not be present").isEmpty();
    }

    @Test
    @DisplayName("deleting parent of body should delete the body.")
    void deletingConstructorShouldDeleteItsBody() {
        var constructor = constructorRepository.save(
                new Constructor("Test", "Test()", "public", false)
        );
        var body = bodyRepository.save(new Body("test{\n}"));
        body.setParent(constructor);
        constructor.setBody(body);
        constructor = constructorRepository.save(constructor);
        constructorRepository.delete(constructor);
        var retrieved = bodyRepository.findByParent(constructor);
        assertThat(retrieved).describedAs("Body should not be present").isEmpty();
    }

    @Test
    @DisplayName("deleting parent of body should delete the body.")
    void deletingMethodShouldDeleteItsBody() {
        var method = methodRepository.save(
                new Method.Builder()
                        .withName("test")
                        .withSignature("Test.test()")
                        .withReturnType("int")
                        .withVisibility("public")
                        .build()
        );
        var body = bodyRepository.save(new Body("test{\n}"));
        body.setParent(method);
        method.setBody(body);
        method = methodRepository.save(method);
        methodRepository.delete(method);
        var retrieved = bodyRepository.findByParent(method);
        assertThat(retrieved).describedAs("Body should not be present").isEmpty();
    }
}