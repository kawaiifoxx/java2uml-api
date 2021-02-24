package org.java2uml.java2umlapi.umlComponenets;

import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import org.apache.commons.io.FileDeleteStrategy;
import org.java2uml.java2umlapi.parser.Parser;
import org.java2uml.java2umlapi.util.unzipper.Unzipper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@DisplayName("When using SourceComponent, ")
class SourceComponentTest {
    private static SourceComponent sourceComponent;
    private static final String PROJECT_ZIP_PATH = "src/test/testSources/ParserTest/test.zip";
    private static final String DST = "src/test/testSources/ParserTest/testOutput";

    @BeforeAll
    static void setUp() throws IOException {
        File generatedSourceFiles = Unzipper.unzipDir(Path.of(PROJECT_ZIP_PATH), Path.of(DST));
        sourceComponent = Parser.parse(generatedSourceFiles.toPath());
    }


    @SuppressWarnings("deprecation")
    @Test
    @DisplayName("using toUML, should generate a valid plant uml syntax.")
    public void testToUML() throws IOException {
        String source = sourceComponent.toUML();
        final ByteArrayOutputStream os;
        try {
            SourceStringReader reader = new SourceStringReader(source);
            os = new ByteArrayOutputStream();
            reader.generateImage(os, new FileFormatOption(FileFormat.SVG));
            os.close();
        } catch (NullPointerException exception) {
            fail("Source component was unable to generate a valid uml syntax, test failed.");
            throw new IllegalStateException("[SourceComponentTest] Invalid Source Component", exception);
        }

        final String svg = os.toString();
        BufferedWriter writer = new BufferedWriter(new FileWriter(DST + "/test.svg"));

        if (svg.contains("Syntax Error?")) {
            fail("Source component was unable to generate a valid uml syntax, test failed.");
            writer.close();
        }


        writer.write(svg);
        writer.close();
    }

    @Test
    @DisplayName("using find, with class name and ParsedClassOrInterfaceComponent.class should " +
            "return ParsedClassOrInterfaceComponent reference for given class.")
    void TestFindClassOrInterface() {
        var toFind = "com.shreyansh.springboot.thymeleafdemo.service.EmployeeService";
        var result = sourceComponent.find(toFind, ParsedClassOrInterfaceComponent.class);

        if (result.isEmpty()) {
            fail("Source component should contain " + toFind);
        }

        assertEquals(toFind, result.get().getName(), "Component name is not equal to passed exactName");
    }

    @Test
    @DisplayName("using find, with enum name and ParsedEnumComponent.class should " +
            "return ParsedEnumComponent reference for given enum.")
    void TestFindEnum() {
        var toFind = "com.shreyansh.springboot.thymeleafdemo.EnumTest";
        var result = sourceComponent.find(toFind, ParsedEnumComponent.class);

        if (result.isEmpty()) {
            fail("Source component should contain " + toFind);
        }

        assertEquals(toFind, result.get().getName(), "Component name is not equal to passed exactName");
    }

    @Test
    @DisplayName("using find, with class name and ParsedExternalComponent.class should " +
            "return ParsedExternalComponent reference for given class.")
    void TestFindExternal() {
        var toFind = "org.springframework.data.jpa.repository.JpaRepository";
        var result = sourceComponent.find(toFind, ParsedExternalComponent.class);

        if (result.isEmpty()) {
            fail("Source component should contain " + toFind);
        }

        assertEquals(toFind, result.get().getName(), "Component name is not equal to passed exactName");
    }

    @Test
    @DisplayName("using find, with method name and ParsedMethodComponent.class should " +
            "return ParsedMethodComponent reference for given method.")
    void TestFindMethod() {
        var toFind = "com.shreyansh.springboot.thymeleafdemo.service.EmployeeServiceImpl.findAll()";
        var result = sourceComponent.find(toFind, ParsedMethodComponent.class);

        if (result.isEmpty()) {
            fail("Source component should contain " + toFind);
        }

        assertEquals(toFind, result.get().getName(), "Component name is not equal to passed exactName");
    }

    @AfterAll
    static void tearDown() throws IOException {
        JarTypeSolver.ResourceRegistry.getRegistry().cleanUp();
        FileDeleteStrategy.FORCE.delete(Path.of(DST).toFile());
    }
}