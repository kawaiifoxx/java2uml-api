package org.java2uml.java2umlapi.visitors.lightWeightExtractor;

import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import org.apache.commons.io.FileDeleteStrategy;
import org.java2uml.java2umlapi.lightWeight.ClassRelation;
import org.java2uml.java2umlapi.lightWeight.Source;
import org.java2uml.java2umlapi.parser.Parser;
import org.java2uml.java2umlapi.umlComponenets.SourceComponent;
import org.java2uml.java2umlapi.util.umlSymbols.RelationsSymbol;
import org.java2uml.java2umlapi.util.unzipper.Unzipper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
@DisplayName("When extracting lightWeights with LightWeightExtractor, ")
class LightWeightExtractorTest {

    private static SourceComponent sourceComponent;
    private static final String PROJECT_TEST = "src/test/testSources/LightWeightExtractorTest/test.zip";
    private static final String DST = "src/test/testSources/LightWeightExtractorTest/testOutput";

    @BeforeAll
    static void setUp() throws IOException {
        sourceComponent = Parser.parse(Unzipper.unzipDir(Path.of(PROJECT_TEST), Path.of(DST)).toPath());
    }

    @Test
    @DisplayName("number of children should be equal to number of classes and enums.")
    void numberOfChildrenInSource() {
        Source source = getSource();

        int numberOfChildren = source.getEnumLWList().size();
        numberOfChildren += source.getClassOrInterfaceList().size();

        //4 classes + 1 interface + 1 external class + 1 enum = 6
        assertEquals(6, numberOfChildren, "actual number of children does not match " +
                "expected number of children.");
    }

    private Source getSource() {
        LightWeightExtractor lightWeightExtractor = new LightWeightExtractor();
        return sourceComponent.accept(lightWeightExtractor).asSource().orElseThrow(
                () -> new UnsupportedOperationException("unable to convert lightWeight to Source.")
        );
    }

    @Test
    @DisplayName("all relations should be extracted correctly")
    void allRelationsAreExtracted() {
        Map<RelationsSymbol, Long> expectedRelations = new HashMap<>();
        expectedRelations.put(RelationsSymbol.AGGREGATION, 2L);
        expectedRelations.put(RelationsSymbol.EXTENSION, 3L);
        expectedRelations.put(RelationsSymbol.DEPENDENCY_AR, 2L);
        Source source = getSource();

        var actualRelations = source
                .getClassRelationList()
                .stream()
                .map(ClassRelation::getRelationsSymbol)
                .collect(Collectors.groupingBy(relationsSymbol -> relationsSymbol, Collectors.counting()));

        assertEquals(expectedRelations, actualRelations, "actual relations does not match expected relations.");

    }

    //TODO: Add more tests.

    @AfterAll
    static void tearDown() throws IOException {
        JarTypeSolver.ResourceRegistry.getRegistry().cleanUp();
        FileDeleteStrategy.FORCE.delete(new File(DST));
    }
}