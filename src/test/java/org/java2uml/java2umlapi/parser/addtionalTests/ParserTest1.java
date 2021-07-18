package org.java2uml.java2umlapi.parser.addtionalTests;

import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import org.apache.commons.io.FileDeleteStrategy;
import org.java2uml.java2umlapi.parser.Parser;
import org.java2uml.java2umlapi.util.unzipper.Unzipper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.fail;

@DisplayName("When using Parser,")
public class ParserTest1 {
    final static String TEST_FILE = "src/test/testSources/ParserTest/addtionalTests/combat-zone-master.zip";
    private static final String DST = "src/test/testOutput";

    @Test
    @DisplayName("using Parser.parse() should parse all files without throwing UnresolvedSymbolException.")
    void testParse() {
        try {
            var dst = Unzipper.unzipDir(Path.of(TEST_FILE), Path.of(DST));
            Parser.parse(dst.toPath());
        } catch (Exception e) {
            fail("Parser.parse() threw exception", e);
        }
    }

    @AfterEach
    void tearDown() throws IOException {
        JarTypeSolver.ResourceRegistry.getRegistry().cleanUp();
        FileDeleteStrategy.FORCE.delete(Path.of(DST).toFile());
    }
}
