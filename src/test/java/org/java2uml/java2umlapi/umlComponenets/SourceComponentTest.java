package org.java2uml.java2umlapi.umlComponenets;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import org.java2uml.java2umlapi.parser.Parser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
class SourceComponentTest {

    @Autowired
    private Parser parser;
    private SourceComponent sourceComponent;
    private static final String MULTIPLE_FILES_PATH = "src/test/testSources/JavaParserFacadeTests/testParserClass/ProjectTest/thymeleaf-demo-thymeleaf-demo";

    @BeforeEach
    void setUp() {
        sourceComponent = parser.parse(Path.of(MULTIPLE_FILES_PATH));
    }


    @SuppressWarnings("deprecation")
    @Test
    @DisplayName("When running toUML, should generate a valid plant uml syntax.")
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
        writer.write(svg);
        writer.close();
    }

}