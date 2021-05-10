package org.java2uml.java2umlapi.lightWeight;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("When building ClassOrInterface with ClassOrInterface.Builder, ")
class ClassOrInterfaceBuilderTest {

    @Test
    @DisplayName("using build, should return an instance of ClassOrInterface.")
    void buildClassOrInterfaceSuccessfully() {
        var classOrInterface = new ClassOrInterface.Builder()
                .withName("Test1")
                .withPackageName("test")
                .withClassConstructors(Collections.emptyList())
                .withMethods(Collections.emptyList())
                .withFields(Collections.emptyList())
                .withTypeParameters(Collections.emptyList())
                .withIsExternal(true)
                .withIsClass(true)
                .withBody(new Body())
                .build();

        assertThat(classOrInterface)
                .describedAs("Has all the properties which were provided to the builder")
                .satisfies((classOrInterface1) -> {
                    assertThat(classOrInterface1.getName()).isEqualTo("Test1");
                    assertThat(classOrInterface1.getPackageName()).isEqualTo("test");
                    assertThat(classOrInterface1.getClassConstructors()).isEmpty();
                    assertThat(classOrInterface1.getClassOrInterfaceMethods()).isEmpty();
                    assertThat(classOrInterface1.getClassFields()).isEmpty();
                    assertThat(classOrInterface1.getClassOrInterfaceTypeParameters()).isEmpty();
                    assertThat(classOrInterface1.isClass()).isTrue();
                    assertThat(classOrInterface1.isExternal()).isTrue();
                    assertThat(classOrInterface1.getBody()).isNotNull();
                });
    }

    @Test
    @DisplayName("using build more than once should throw UnsupportedOperationException.")
    void callingBuildMoreThanOnce() {
        assertThatThrownBy(() -> {
            var builder = new ClassOrInterface.Builder();
            builder.build();
            builder.build();
        })
                .describedAs("should throw, UnsupportedOperationException.")
                .isInstanceOf(UnsupportedOperationException.class);
    }
}