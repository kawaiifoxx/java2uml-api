package org.java2uml.java2umlapi.parser;

import com.github.javaparser.resolution.declarations.ResolvedDeclaration;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import org.apache.commons.io.FileDeleteStrategy;
import org.java2uml.java2umlapi.util.unzipper.Unzipper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("When using Parser,")
class ParserTest {
    private List<ResolvedDeclaration> resolvedDeclarations;
    private Set<String> expectedClassNames;
    private static final String PROJECT_TEST = "src/test/testSources/JavaParserFacadeTests/testParserClass/ProjectTest/test.zip";
    private static final String DST = "src/test/testOutput";


    @BeforeEach
    void setUp() throws IOException {
        var destDir = Unzipper.unzipDir(Path.of(PROJECT_TEST), Path.of(DST));
        var sourceComponent = Parser.parse(destDir.toPath());

        expectedClassNames = new HashSet<>();
        expectedClassNames.add("com.shreyansh.springboot.thymeleafdemo.controller.EmployeeController");
        expectedClassNames.add("com.shreyansh.springboot.thymeleafdemo.dao.EmployeeRepository");
        expectedClassNames.add("com.shreyansh.springboot.thymeleafdemo.entity.Employee");
        expectedClassNames.add("com.shreyansh.springboot.thymeleafdemo.ThymeleafDemoApplication");
        expectedClassNames.add("com.shreyansh.springboot.thymeleafdemo.EnumTest");
        expectedClassNames.add("com.shreyansh.springboot.thymeleafdemo.service.EmployeeService");
        expectedClassNames.add("com.shreyansh.springboot.thymeleafdemo.service.EmployeeServiceImpl");
        expectedClassNames.add("MavenWrapperDownloader");
        expectedClassNames.add("com.shreyansh.springboot.thymeleafdemo.ThymeleafDemoApplicationTests");


        resolvedDeclarations = sourceComponent.getAllParsedTypes();
    }

    @Test
    @DisplayName("using parse, should return correct resolved declaration.")
    void testParse() {
        Set<String> actualClassNames = new HashSet<>();

        resolvedDeclarations.forEach(resolvedDeclaration -> actualClassNames.add(resolvedDeclaration.asType().getQualifiedName()));

        assertEquals(expectedClassNames, actualClassNames, "expected classes names did not match actual class names.");
    }

    @AfterEach
    private void tearDown() throws IOException {
        JarTypeSolver.ResourceRegistry.getRegistry().cleanUp();
        FileDeleteStrategy.FORCE.delete(Path.of(DST).toFile());
    }
}