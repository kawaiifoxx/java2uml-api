package org.java2uml.java2umlapi.umlComponenets;

import org.java2uml.java2umlapi.parser.Parser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
class SourceComponentTest {

    @Autowired
    private Parser parser;
    private SourceComponent sourceComponent;
    private static final String MULTIPLE_FILES_PATH = "src/test/testSources/JavaParserFacadeTests/testParserClass/MultipleFileTest/multipleFilesTest";

    @BeforeEach
    void setUp() {
        var resolvedTypeList = parser.getAllResolvedDeclarations(MULTIPLE_FILES_PATH);

        sourceComponent = new SourceComponent(resolvedTypeList);
    }

    //TODO: FINISH THIS!
    @Test
    public void TestToString() {
        System.out.println(sourceComponent);
        fail();
    }

}