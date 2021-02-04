package org.java2uml.java2umlapi.parser;

import com.github.javaparser.resolution.declarations.ResolvedDeclaration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ParserTest {

    @Autowired
    private Parser parser;
    private List<ResolvedDeclaration> resolvedDeclarations;
    private Set<String> expectedClassNames;
    private static final String PROJECT_TEST = "src/test/testSources/JavaParserFacadeTests/testParserClass/ProjectTest/thymeleaf-demo-thymeleaf-demo";


    @BeforeEach
    void setUp() {
        var sourceComponent = parser.parse(Path.of(PROJECT_TEST));

        expectedClassNames = new HashSet<>();
        expectedClassNames.add("com.shreyansh.springboot.thymeleafdemo.controller.EmployeeController");
        expectedClassNames.add("com.shreyansh.springboot.thymeleafdemo.dao.EmployeeRepository");
        expectedClassNames.add("com.shreyansh.springboot.thymeleafdemo.entity.Employee");
        expectedClassNames.add("com.shreyansh.springboot.thymeleafdemo.ThymeleafDemoApplication");
        expectedClassNames.add("com.shreyansh.springboot.thymeleafdemo.service.EmployeeService");
        expectedClassNames.add("com.shreyansh.springboot.thymeleafdemo.service.EmployeeServiceImpl");
        expectedClassNames.add("MavenWrapperDownloader");
        expectedClassNames.add("com.shreyansh.springboot.thymeleafdemo.ThymeleafDemoApplicationTests");


        resolvedDeclarations = sourceComponent.getAllParsedTypes();
    }

    @Test
    @DisplayName("When parser parses code, should give fullyQualified classNames.")
    void testParser() {
        Set<String> actualClassNames = new HashSet<>();

        resolvedDeclarations.forEach(resolvedDeclaration -> actualClassNames.add(resolvedDeclaration.asType().getQualifiedName()));

        assertEquals(expectedClassNames, actualClassNames, "expected classes names did not match actual class names.");
    }
}