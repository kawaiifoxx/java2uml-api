package org.java2uml.java2umlapi.parser;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class ClassOrInterfaceCollectorTest {

    CompilationUnit cu;
    Set<String> expectedSet;

    @BeforeEach
    void setUp() {
        cu = StaticJavaParser.parse("package com.shreyansh.ex1;\n" +
                "\n" +
                "public class TestClass {\n" +
                "    public static class innerClass {\n" +
                "        public interface innerInnerInterface {\n" +
                "\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    public class innerClass2 {\n" +
                "        public abstract class innerInnerAbstractClass {\n" +
                "            \n" +
                "        }\n" +
                "    }\n" +
                "}\n");

        expectedSet = new HashSet<>();
        expectedSet.add("com.shreyansh.ex1.TestClass");
        expectedSet.add("com.shreyansh.ex1.TestClass.innerClass");
        expectedSet.add("com.shreyansh.ex1.TestClass.innerClass.innerInnerInterface");
        expectedSet.add("com.shreyansh.ex1.TestClass.innerClass2");
        expectedSet.add("com.shreyansh.ex1.TestClass.innerClass2.innerInnerAbstractClass");

    }

    @Test
    @DisplayName("tests visit returns fully qualified classOrInterface names,")
    void testVisit() {
        VoidVisitor<Map<String, ClassOrInterfaceDeclaration>> visitor = new ClassOrInterfaceCollector();

        Map<String, ClassOrInterfaceDeclaration> state = new HashMap<>();

        visitor.visit(cu, state);

        Set<String> resultSet = new HashSet<>();
        state.forEach((k, v) -> resultSet.add(k));

        assertEquals(expectedSet, resultSet, "ClassOrInterfaceDeclaration does not return correct classOrInterface names.");

    }


}