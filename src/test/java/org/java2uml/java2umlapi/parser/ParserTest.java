package org.java2uml.java2umlapi.parser;

import com.github.javaparser.resolution.declarations.ResolvedTypeDeclaration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class JavaParserFacadeTest {

    @Autowired
    private JavaParserFacade jpf;
    private Set<String> expectedSet;
    private static final String SINGLE_FILE_PATH = "src/test/testSources/JavaParserFacadeTests/testParserClass/SingleFileTest/TestClass.java";
    private static final String MULTIPLE_FILES_PATH = "src/test/testSources/JavaParserFacadeTests/testParserClass/MultipleFileTest/multipleFilesTest";

    @BeforeEach
    void setUp() {
        expectedSet = new HashSet<>();
        expectedSet.add("com.shreyansh.ex1.TestClass");
        expectedSet.add("com.shreyansh.ex1.TestClass.innerClass");
        expectedSet.add("com.shreyansh.ex1.TestClass.innerClass.innerInnerInterface");
        expectedSet.add("com.shreyansh.ex1.TestClass.innerClass2");
        expectedSet.add("com.shreyansh.ex1.TestClass.innerClass2.innerInnerAbstractClass");

    }

    @Test
    void testParseClassesWithSingleFile() {
        var resultList = jpf.parseClasses(SINGLE_FILE_PATH);

        assertEquals(expectedSet, new HashSet<>(resultList), "In single file, JavaParserFacade was not able to collect all the classes properly.");
    }

    @Test
    void testParseClassesWithMultipleFiles() {
        addMultipleClassOrInterfaceDeclarationToExpectedSet();
        var resultList = jpf.parseClasses(MULTIPLE_FILES_PATH);

        assertEquals(expectedSet, new HashSet<>(resultList), "In Multiple files, JavaParserFacade was not able to collect all the classes properly.");
    }

    @Test
    @DisplayName("when solving reference types, expect all reference types to be resolved and added to the list.")
    void testGetAllResolvedReferenceTypes() {
        addMultipleClassOrInterfaceDeclarationToExpectedSet();
        var resultList = jpf.getAllResolvedReferenceTypes(MULTIPLE_FILES_PATH);

        assertEquals(expectedSet, new HashSet<>(resultList.stream()
                .map(ResolvedTypeDeclaration::getQualifiedName)
                .collect(Collectors.toList())), "unable to solve all references correctly");
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