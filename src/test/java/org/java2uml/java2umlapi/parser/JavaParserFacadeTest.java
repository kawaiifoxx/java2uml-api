package org.java2uml.java2umlapi.parser;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.java2uml.java2umlapi.util.DirExplorer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JavaParserFacadeTest {

    @Autowired
    private JavaParserFacade jpf;
    private Set<String> expectedSet;
    private Set<String> resultSet;

    @BeforeEach
    void setUp() {
        expectedSet = new HashSet<>();
        resultSet = new HashSet<>();
        expectedSet.add("com.shreyansh.ex1.TestClass");
        expectedSet.add("com.shreyansh.ex1.TestClass.innerClass");
        expectedSet.add("com.shreyansh.ex1.TestClass.innerClass.innerInnerInterface");
        expectedSet.add("com.shreyansh.ex1.TestClass.innerClass2");
        expectedSet.add("com.shreyansh.ex1.TestClass.innerClass2.innerInnerAbstractClass");

    }

    @Test
    void testParseClassesWithSingleFile() {
        var resultMap = jpf.parseClasses("src/test/testSources/JavaParserFacadeTests/testParserClass/SingleFileTest/TestClass.java");


        resultMap.forEach((k, v) -> resultSet.add(k));

        assertEquals(expectedSet, resultSet, "In single file, JavaParserFacade was not able to collect all the classes properly.");
    }

    @Test
    void testParseClassesWithMultipleFiles() {
        addMultipleClassOrInterfaceDeclarationToExpectedSet();
        var resultMap = jpf.parseClasses("src/test/testSources/JavaParserFacadeTests/testParserClass/MultipleFileTest");

        resultMap.forEach((k, v) -> resultSet.add(k));

        assertEquals(expectedSet, resultSet, "In Multiple files, JavaParserFacade was not able to collect all the classes properly.");
    }

    private void addMultipleClassOrInterfaceDeclarationToExpectedSet() {
        expectedSet.add("com.shreyansh.ex1.GetComments");
        expectedSet.add("com.shreyansh.ex1.GetComments.CommentReportEntry");
        expectedSet.add("com.shreyansh.ex1.MethodNameCollector");
        expectedSet.add("com.shreyansh.ex1.MethodNamePrinter");
        expectedSet.add("com.shreyansh.ex1.ModifyingVisitorComplete");
        expectedSet.add("com.shreyansh.ex1.ModifyingVisitorComplete.IntegerLiteralModifier");
        expectedSet.add("com.shreyansh.ex1.SimpleVisitor");
    }
}