package org.java2uml.java2umlapi.umlComponenets;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import org.apache.commons.io.FileDeleteStrategy;
import org.java2uml.java2umlapi.parser.Parser;
import org.java2uml.java2umlapi.util.unzipper.Unzipper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.fail;

class SourceComponentTest {
    private SourceComponent sourceComponent;
    private static final String MULTIPLE_FILES_PATH = "src/test/testSources/JavaParserFacadeTests/testParserClass/ProjectTest/thymeleaf-demo-thymeleaf-demo";
    private static final String PROJECT_ZIP_PATH = "src/test/testSources/JavaParserFacadeTests/testParserClass/ProjectTest/thymeleaf-demo-thymeleaf-demo.zip";
    private static final String DST = "src/test/testOutput";
    private File generatedSourceFiles;

    @BeforeEach
    void setUp() throws IOException {
        generatedSourceFiles = Unzipper.unzipDir(Path.of(PROJECT_ZIP_PATH), Path.of(DST));
        sourceComponent = Parser.parse(generatedSourceFiles.toPath());
    }


    @SuppressWarnings("deprecation")
    @Test
    @DisplayName("When executing toUML, should generate a valid plant uml syntax.")
    public void testToUML() throws IOException {
        String source = sourceComponent.toUML();
        final ByteArrayOutputStream os;
        try {
            SourceStringReader reader = new SourceStringReader(source);
            os = new ByteArrayOutputStream();
            var desc = reader.generateImage(os, new FileFormatOption(FileFormat.SVG));
            os.close();
        } catch (NullPointerException exception) {
            fail("Source component was unable to generate a valid uml syntax, test failed.");
            throw new RuntimeException("[SourceComponentTest] Invalid Source Component");
        }

        final String svg = os.toString();
        BufferedWriter writer = new BufferedWriter(new FileWriter("src/test/testOutput/test.svg"));

        if (svg.contains("Syntax Error?")) {
            fail("Source component was unable to generate a valid uml syntax, test failed.");
            writer.close();
        }


        writer.write(svg);
        writer.close();
    }

    @AfterEach
    private void tearDown() throws IOException {
        FileDeleteStrategy.FORCE.delete(generatedSourceFiles);
    }
}