package org.java2uml.java2umlapi.lightWeight;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("When constructing method using Builder, ")
class MethodBuilderTest {
    @Test
    @DisplayName("given that all the arguments are provided method should build successfully")
    void buildMethodSuccessfully() {
        var method = new  Method.Builder()
                .withName("test")
                .withPackageName("com.test")
                .withSignature("test()")
                .withReturnType("int")
                .withVisibility("public")
                .withParameters(new ArrayList<>())
                .withTypeParameters(new ArrayList<>())
                .withSpecifiedExceptions(new ArrayList<>())
                .withStatic(false)
                .withParent(null)
                .withBody(new Body("{\n}"))
                .build();


        assertThat(method).describedAs("contains all the added properties.")
                .satisfies(method1 -> assertThat(method1.getName()).isEqualTo("test"))
                .satisfies(method1 -> assertThat(method1.getPackageName()).isEqualTo("com.test"))
                .satisfies(method1 -> assertThat(method1.getSignature()).isEqualTo("test()"))
                .satisfies(method1 -> assertThat(method1.getReturnType()).isEqualTo("int"))
                .satisfies(method1 -> assertThat(method1.getVisibility()).isEqualTo("public"));
    }

    @Test
    @DisplayName("given that some required parameters are not added while building method, build() method should " +
            "throw IllegalStateException.")
    void buildMethodThrowsException() {
        assertThatThrownBy(() -> new Method.Builder().withName("test").withSignature("test()").build())
                .isInstanceOf(IllegalStateException.class);
    }
}