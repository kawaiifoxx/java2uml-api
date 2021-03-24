package org.java2uml.java2umlapi.visitors.lightWeightExtractor.specialized;

import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import org.apache.commons.io.FileDeleteStrategy;
import org.java2uml.java2umlapi.lightWeight.repository.MethodRepository;
import org.java2uml.java2umlapi.parsedComponent.SourceComponent;
import org.java2uml.java2umlapi.parser.Parser;
import org.java2uml.java2umlapi.restControllers.exceptions.LightWeightNotFoundException;
import org.java2uml.java2umlapi.util.unzipper.Unzipper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("When using LightWeightExtractorWithMethodSignatureCache, ")
@DataJpaTest
class LightWeightExtractorWithMethodSignatureCacheTest {

    private static SourceComponent sourceComponent;
    @Autowired
    private MethodRepository repository;
    private static final String PROJECT_TEST = "src/test/testSources/LightWeightExtractorTest/test.zip";
    private static final String DST = "src/test/testSources/LightWeightExtractorTest/testOutput";

    @BeforeAll
    static void setUp() throws IOException {
        sourceComponent = Parser.parse(Unzipper.unzipDir(Path.of(PROJECT_TEST), Path.of(DST)).toPath());
    }

    @AfterAll
    static void tearDown() throws IOException {
        JarTypeSolver.ResourceRegistry.getRegistry().cleanUp();
        FileDeleteStrategy.FORCE.delete(new File(DST));
    }

    @Test
    @DisplayName("method repository should not be null.")
    void methodRepositoryShouldNotBeNull() {
        assertThat(repository).describedAs("repository should not be null").isNotNull();
    }

    @Test
    @DisplayName("using get signature to id map should return a map which contains mapping from signature to ids.")
    void getSignatureToIdMap() {
        var visitor = new LightWeightExtractorWithMethodSignatureCache(repository);
        var source = sourceComponent.accept(visitor).asSource().orElseThrow(
                () -> new LightWeightNotFoundException(
                        "Unable to get source, something is wrong with the" +
                                "LightWeightExtractorWithMethodSignatureCache"
                )
        );

        var actualMap = visitor.getSignatureToIdMap();

        var expectedMethodNames = Set.of(
                "Test1.method1()",
                "Test1.method2()",
                "Test1.method3()",
                "Test1.method4()",
                "Test2.method1()",
                "Test2.method2()",
                "Test2.method3()",
                "Test2.method4()",
                "Test3.method1(Test1)",
                "Test3.method2(Test4)",
                "TestEnum.getVal()"
        );

        assertThat(actualMap.values())
                .describedAs("any values should not be null.")
                .allSatisfy(value -> assertThat(value).isNotNull());

        assertThat(actualMap.keySet())
                .describedAs("expected method signatures should match actual method signatures.")
                .isEqualTo(expectedMethodNames);
    }
}