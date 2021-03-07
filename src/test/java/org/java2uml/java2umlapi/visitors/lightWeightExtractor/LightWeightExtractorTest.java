package org.java2uml.java2umlapi.visitors.lightWeightExtractor;

import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import org.apache.commons.io.FileDeleteStrategy;
import org.java2uml.java2umlapi.lightWeight.*;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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


    @Test
    @DisplayName("number of methods should be equal to total number of methods in all the classes and enums.")
    void numberOfMethods() {
        var source = getSource();

        var actualNumberOfMethods = source
                .getClassOrInterfaceList()
                .stream()
                .mapToInt(classOrInterface -> classOrInterface.getMethods().size())
                .sum();
        actualNumberOfMethods += source
                .getEnumLWList()
                .stream()
                .mapToInt(enumLW -> enumLW.getMethods().size())
                .sum();

        assertEquals(11, actualNumberOfMethods, "actual number of methods does " +
                "not match expected number of methods.");
    }

    @Test
    @DisplayName("number of constructors should be equal to total number of constructors in all the classes and enums.")
    void numberOfConstructors() {
        var source = getSource();

        var actualNumberOfConstructors = source
                .getClassOrInterfaceList()
                .stream()
                .mapToInt(classOrInterface -> classOrInterface.getConstructors().size())
                .sum();
        actualNumberOfConstructors += source
                .getEnumLWList()
                .stream()
                .mapToInt(enumLW -> enumLW.getConstructors().size())
                .sum();

        assertEquals(4, actualNumberOfConstructors, "actual number of constructors does not " +
                "match expected number of constructors.");
    }

    @Test
    @DisplayName("number of fields should be equal to total number of fields in all the classes and enums.")
    void numberOfFields() {
        var source = getSource();

        var actualNumberOfFields = source
                .getClassOrInterfaceList()
                .stream()
                .mapToInt(classOrInterface -> classOrInterface.getFields().size())
                .sum();
        actualNumberOfFields += source
                .getEnumLWList()
                .stream()
                .mapToInt(enumLW -> enumLW.getFields().size())
                .sum();

        assertEquals(8, actualNumberOfFields, "actual number of fields does not " +
                "match expected number of fields.");
    }

    @Test
    @DisplayName("number of Enum Constants should be equal to total number of Enum Constants in all the enums.")
    void numberOfEnumConstants() {
        var source = getSource();

        var actualNumberOfEnumConstants = source
                .getEnumLWList()
                .stream()
                .mapToInt(enumLW -> enumLW.getEnumConstants().size())
                .sum();

        assertEquals(3, actualNumberOfEnumConstants, "actual number of Enum Constants does not " +
                "match expected number of Enum Constants.");
    }

    @Test
    @DisplayName("name of classes should be same as in the source files.")
    void nameOfClasses() {
        Set<String> classNames = Set.of(
                "Test1",
                "Test2",
                "Test3",
                "Test4",
                "java.io.Serializable"
        );

        var source = getSource();

        var actualClassNames = source
                .getClassOrInterfaceList()
                .stream()
                .map(ClassOrInterface::getName)
                .collect(Collectors.toSet());

        assertEquals(classNames, actualClassNames, "all the class names should be same as in the source files.");
    }

    @Test
    @DisplayName("name of enums should be same as in the source files.")
    void nameOfEnums() {
        Set<String> enumNames = Set.of(
                "TestEnum"
        );

        var source = getSource();

        var actualEnumNames = source
                .getEnumLWList()
                .stream()
                .map(EnumLW::getName)
                .collect(Collectors.toSet());

        assertEquals(enumNames, actualEnumNames, "all the enum names should be same as in the source files.");
    }

    @Test
    @DisplayName("all the external classes should be marked as external.")
    void externalClassShouldBeMarked() {
        var source = getSource();

        var externalClasses = source
                .getClassOrInterfaceList()
                .stream()
                .filter(classOrInterface -> classOrInterface.getName().equals("java.io.Serializable"))
                .collect(Collectors.toList());

        externalClasses
                .forEach(
                        externalClass -> assertTrue(externalClass.isExternal(),
                                "all external classes should be marked as external")
                );
    }

    @Test
    @DisplayName("name of methods should be same as in the source files.")
    void nameOfMethods() {
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
        var source = getSource();
        var actualMethodNames = source
                .getClassOrInterfaceList()
                .stream()
                .map(ClassOrInterface::getMethods)
                .flatMap(Collection::stream)
                .map(Method::getName)
                .collect(Collectors.toSet());

        actualMethodNames.addAll(
                source
                        .getEnumLWList()
                        .stream()
                        .map(EnumLW::getMethods)
                        .flatMap(Collection::stream)
                        .map(Method::getName)
                        .collect(Collectors.toSet())
        );


        assertEquals(expectedMethodNames, actualMethodNames,
                "expected method names does not match actual method names");
    }

    @Test
    @DisplayName("name of constructors should be same as in the source files.")
    void nameOfConstructor() {
        var expectedConstructorNames = Set.of(
                "Test3.Test3(Test1, Test2, int, java.lang.String)",
                "Test2.Test2()",
                "Test4.Test4(Test1, Test2, int, java.lang.String)",
                "TestEnum.TestEnum(int)"
        );
        var source = getSource();
        var actualConstructorNames = source
                .getClassOrInterfaceList()
                .stream()
                .map(ClassOrInterface::getConstructors)
                .flatMap(Collection::stream)
                .map(Constructor::getName)
                .collect(Collectors.toSet());

        actualConstructorNames.addAll(
                source
                        .getEnumLWList()
                        .stream()
                        .map(EnumLW::getConstructors)
                        .flatMap(Collection::stream)
                        .map(Constructor::getName)
                        .collect(Collectors.toSet())
        );


        assertEquals(expectedConstructorNames, actualConstructorNames,
                "expected constructors names does not match actual constructors names");
    }

    @Test
    @DisplayName("names of fields should be same as in the source files.")
    void nameOfFields() {
        var expectedFieldNames = Map.of(
                "field1",
                2L,
                "field3",
                2L,
                "field2",
                2L,
                "field4",
                1L,
                "val",
                1L
        );

        var source = getSource();
        var actualFieldNames = source
                .getClassOrInterfaceList()
                .stream()
                .map(ClassOrInterface::getFields)
                .flatMap(Collection::stream)
                .map(Field::getName)
                .collect(Collectors.groupingBy(name -> name, Collectors.counting()));

        actualFieldNames.putAll(
                source
                        .getEnumLWList()
                        .stream()
                        .map(EnumLW::getFields)
                        .flatMap(Collection::stream)
                        .map(Field::getName)
                        .collect(Collectors.groupingBy(name -> name, Collectors.counting()))
        );


        assertEquals(expectedFieldNames, actualFieldNames,
                "expected fields names does not match actual fields names");
    }

    @Test
    @DisplayName("names of enumConstants should be same as in the source files.")
    void nameOfEnumConstant() {
        var expectedEnumConstants = Set.of(
                "TestEnum.HIGH",
                "TestEnum.MED",
                "TestEnum.LOW"
        );

        var actualEnumConstants = getSource()
                .getEnumLWList()
                .stream()
                .map(EnumLW::getEnumConstants)
                .flatMap(Collection::stream)
                .map(EnumConstant::getName)
                .collect(Collectors.toSet());

        assertEquals(expectedEnumConstants, actualEnumConstants, "expected enum constants should match " +
                "actual enum constants in source files.");
    }

    @Test
    @DisplayName("class should have a body.")
    void classBody() {
        var expectedBodies = Set.of(
                "publicinterfaceTest1{voidmethod1();voidmethod2();voidmethod3();voidmethod4();}",
                "java.io.Serializable{}",
                "publicclassTest2implementsTest1,Serializable{intfield1;intfield2;intfield3;" +
                        "@Overridepublicvoidmethod1(){}@Overridepublicvoidmethod2(){}@Overri" +
                        "depublicvoidmethod3(){}@Overridepublicvoidmethod4(){}}",
                "publicclassTest3{Test1field1;Test2field2;intfield3;Stringfield4;publicTest3" +
                        "(Test1field1,Test2field2,intfield3,Stringfield4){this.field1=field1;" +
                        "this.field2=field2;this.field3=field3;this.field4=field4;}publicvoid" +
                        "method1(Test1field1){}publicvoidmethod2(Test4field5){}}",
                "publicclassTest4extendsTest3{publicTest4(Test1field1,Test2field2,intfield3,St" +
                        "ringfield4){super(field1,field2,field3,field4);}}"
        );

        Pattern pattern = Pattern.compile("\\s", Pattern.MULTILINE);

        var actualBodies = getSource()
                .getClassOrInterfaceList()
                .stream()
                .map(ClassOrInterface::getBody)
                .map(Body::getContent)
                .map(string -> {
                    Matcher matcher = pattern.matcher(string);
                    return matcher.replaceAll("");
                })
                .collect(Collectors.toSet());


        assertEquals(expectedBodies, actualBodies, "expected bodies should match actualBodies");
    }

    @Test
    @DisplayName("method should have a body.")
    void methodBody() {
        var expectedBodies = Set.of(
                "publicvoidmethod2(Test4field5){}",
                "voidmethod2();",
                "voidmethod3();",
                "voidmethod1();",
                "@Overridepublicvoidmethod1(){}",
                "voidmethod4();",
                "publicvoidmethod1(Test1field1){}",
                "@Overridepublicvoidmethod4(){}",
                "@Overridepublicvoidmethod3(){}",
                "@Overridepublicvoidmethod2(){}",
                "publicintgetVal(){returnval;}"
        );

        Pattern pattern = Pattern.compile("\\s", Pattern.MULTILINE);

        var source = getSource();
        var actualBodies = source
                .getClassOrInterfaceList()
                .stream()
                .map(ClassOrInterface::getMethods)
                .flatMap(Collection::stream)
                .map(Method::getBody)
                .map(Body::getContent)
                .map(string -> {
                    Matcher matcher = pattern.matcher(string);
                    return matcher.replaceAll("");
                })
                .collect(Collectors.toSet());

        actualBodies.addAll(
                source
                        .getEnumLWList()
                        .stream()
                        .map(EnumLW::getMethods)
                        .flatMap(Collection::stream)
                        .map(Method::getBody)
                        .map(Body::getContent)
                        .map(string -> {
                            Matcher matcher = pattern.matcher(string);
                            return matcher.replaceAll("");
                        })
                        .collect(Collectors.toSet())
        );


        assertEquals(expectedBodies, actualBodies, "expected bodies should match actualBodies");
    }

    @Test
    @DisplayName("constructor should have a body.")
    void constructorBody() {
        var expectedBodies = Set.of(
                "publicTest4(Test1field1,Test2field2,intfield3,Stringfield4){super(field1,field2,field3,field4);}",
                "publicTest3(Test1field1,Test2field2,intfield3,Stringfield4){this.field1=field1;this.field2=f" +
                        "ield2;this.field3=field3;this.field4=field4;}",
                "Test2(){}",
                "TestEnum(intval){this.val=val;}"
        );

        Pattern pattern = Pattern.compile("\\s", Pattern.MULTILINE);

        var source = getSource();

        var actualBodies = source
                .getClassOrInterfaceList()
                .stream()
                .map(ClassOrInterface::getConstructors)
                .flatMap(Collection::stream)
                .map(Constructor::getBody)
                .map(Body::getContent)
                .map(string -> {
                    Matcher matcher = pattern.matcher(string);
                    return matcher.replaceAll("");
                })
                .collect(Collectors.toSet());

        actualBodies.addAll(
                source
                        .getEnumLWList()
                        .stream()
                        .map(EnumLW::getConstructors)
                        .flatMap(Collection::stream)
                        .map(Constructor::getBody)
                        .map(Body::getContent)
                        .map(string -> {
                            Matcher matcher = pattern.matcher(string);
                            return matcher.replaceAll("");
                        })
                        .collect(Collectors.toSet())
        );

        assertEquals(expectedBodies, actualBodies, "expected bodies should match actualBodies");
    }

    @AfterAll
    static void tearDown() throws IOException {
        JarTypeSolver.ResourceRegistry.getRegistry().cleanUp();
        FileDeleteStrategy.FORCE.delete(new File(DST));
    }
}