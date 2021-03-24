package org.java2uml.java2umlapi.callGraph;

import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import org.apache.commons.io.FileDeleteStrategy;
import org.java2uml.java2umlapi.parsedComponent.ParsedMethodComponent;
import org.java2uml.java2umlapi.parsedComponent.SourceComponent;
import org.java2uml.java2umlapi.parser.Parser;
import org.java2uml.java2umlapi.util.unzipper.Unzipper;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@DisplayName("When using MethodCallGraphImpl,")
class MethodCallGraphImplTest {
    private static final String PROJECT_ZIP_PATH = "src/test/testSources/callGraphTest/test.zip";
    private static final String DST = "src/test/testSources/callGraphTest/testOutput";
    private static MethodCallGraphImpl methodCallGraph;
    private static SourceComponent sourceComponent;

    @BeforeAll
    static void setUp() throws IOException {
        File generatedSourceFiles = Unzipper.unzipDir(Path.of(PROJECT_ZIP_PATH), Path.of(DST));
        sourceComponent = Parser.parse(generatedSourceFiles.toPath());
    }

    @AfterAll
    static void tearDown() throws IOException {
        JarTypeSolver.ResourceRegistry.getRegistry().cleanUp();
        FileDeleteStrategy.FORCE.delete(Path.of(DST).toFile());
    }

    void extractAllMethodNames(CallGraphNode currNode, Set<String> acc) {
        acc.add(currNode.getName());
        currNode.getNeighbors()
                .stream()
                .filter(child -> !acc.contains(child.getName()))
                .forEach(child -> extractAllMethodNames(child, acc));
    }

    @Nested
    @DisplayName("given call graph is basic, ")
    class NormalCallGraph {
        private Set<String> expectedMethodSet;
        private Map<String, List<String>> expectedCallGraphMap;
        String plantUMLMindMap;
        private List<CallGraphRelation> expectedCallGraphRelations;

        @BeforeEach
        void setUp() {
            ParsedMethodComponent parsedMethodComponent = sourceComponent.find("co.test.callGraphTest.normal.Test5.test2()",
                    ParsedMethodComponent.class).orElseThrow(() -> new RuntimeException("Unable to find given method in SourceComponent."));
            methodCallGraph = new MethodCallGraphImpl(parsedMethodComponent.getAsResolvedMethodDeclaration()
                    .orElseThrow(() -> new IllegalStateException("ParsedMethodComponent does not contain ResolvedMethodDeclaration")), "co.test.callGraphTest.normal");

            expectedMethodSet = new HashSet<>();
            expectedMethodSet.add("co.test.callGraphTest.normal.Test5.test2()");
            expectedMethodSet.add("co.test.callGraphTest.normal.Test4.test4()");
            expectedMethodSet.add("co.test.callGraphTest.normal.Test3.test3()");
            expectedMethodSet.add("co.test.callGraphTest.normal.Test2.test2()");
            expectedMethodSet.add("co.test.callGraphTest.normal.Test1_1.test1()");
            expectedMethodSet.add("co.test.callGraphTest.normal.Test1_2.test1()");

            expectedCallGraphMap = new HashMap<>();

            expectedCallGraphMap.put("co.test.callGraphTest.normal.Test5.test2()", List.of("co.test.callGraphTest.normal.Test4.test4()"));
            expectedCallGraphMap.put("co.test.callGraphTest.normal.Test4.test4()", List.of("co.test.callGraphTest.normal.Test3.test3()"));
            expectedCallGraphMap.put("co.test.callGraphTest.normal.Test3.test3()", List.of("co.test.callGraphTest.normal.Test2.test2()"));
            expectedCallGraphMap.put("co.test.callGraphTest.normal.Test2.test2()", List.of("co.test.callGraphTest.normal.Test1_1.test1()",
                    "co.test.callGraphTest.normal.Test1_2.test1()"));
            expectedCallGraphMap.put("co.test.callGraphTest.normal.Test1_1.test1()", List.of());
            expectedCallGraphMap.put("co.test.callGraphTest.normal.Test1_2.test1()", List.of());

            expectedCallGraphRelations =  List.of(
                    new CallGraphRelation(
                            "co.test.callGraphTest.normal.Test5.test2()",
                            "co.test.callGraphTest.normal.Test4.test4()"
                    ),
                    new CallGraphRelation(
                            "co.test.callGraphTest.normal.Test4.test4()",
                            "co.test.callGraphTest.normal.Test3.test3()"
                    ),
                    new CallGraphRelation(
                            "co.test.callGraphTest.normal.Test3.test3()",
                            "co.test.callGraphTest.normal.Test2.test2()"
                    ),
                    new CallGraphRelation(
                            "co.test.callGraphTest.normal.Test2.test2()",
                            "co.test.callGraphTest.normal.Test1_1.test1()"
                    ),
                    new CallGraphRelation(
                            "co.test.callGraphTest.normal.Test2.test2()",
                            "co.test.callGraphTest.normal.Test1_2.test1()"
                    )
            );

            plantUMLMindMap = "@startmindmap\n" +
                    "* co.test.callGraphTest.normal.Test5.test2()\n" +
                    "** co.test.callGraphTest.normal.Test4.test4()\n" +
                    "*** co.test.callGraphTest.normal.Test3.test3()\n" +
                    "**** co.test.callGraphTest.normal.Test2.test2()\n" +
                    "***** co.test.callGraphTest.normal.Test1_1.test1()\n" +
                    "***** co.test.callGraphTest.normal.Test1_2.test1()\n" +
                    "@endmindmap";
        }

        @Test
        @DisplayName("using getCallGraphRelations, should return all the relations of call graph.")
        void testGetCallGraphRelations() {
            var actualCallGraphRelations = methodCallGraph.getCallGraphRelations(new HashMap<>());
            assertThat(new HashSet<>(actualCallGraphRelations))
                    .describedAs("actual call graph relations should be same as expected call graph relations.")
                    .isEqualTo(new HashSet<>(expectedCallGraphRelations));
        }

        @Test
        @DisplayName("using getAllResolvedMethodDeclarations, should return all RMDs present in the call graph.")
        void testGetAllResolvedMethodDeclarations() {
            var allMethods = methodCallGraph.getAllResolvedMethodDeclarations();
            assertEquals(expectedMethodSet, allMethods.stream()
                    .map(ResolvedMethodDeclaration::getQualifiedSignature)
                    .collect(Collectors.toSet()));
        }

        @Test
        @DisplayName("using getCallGraph, should return root node of the call graph.")
        void testGetCallGraph() {
            var root = methodCallGraph.getCallGraph();
            var actualMethodSet = new HashSet<String>();
            extractAllMethodNames(root, actualMethodSet);
            assertEquals(expectedMethodSet, actualMethodSet);
        }

        @Test
        @DisplayName("using getCallGraphMap, should return mapping b/w Node's name -> Node's children's names.")
        void testGetCallGraphMap() {
            var actualCallGraphMap = methodCallGraph.getCallGraphMap();
            assertEquals(expectedCallGraphMap, actualCallGraphMap);
        }

        @Test
        @DisplayName("using getCallGraphString, should return mind map code for plant uml.")
        void testGetPlantUMLMindMap() throws IOException {
            String source = methodCallGraph.getPlantUMLMindMap();
            final ByteArrayOutputStream os;
            try {
                SourceStringReader reader = new SourceStringReader(source);
                os = new ByteArrayOutputStream();
                //noinspection deprecation
                reader.generateImage(os, new FileFormatOption(FileFormat.SVG));
                os.close();
            } catch (NullPointerException exception) {
                fail("Source component was unable to generate a valid uml syntax, test failed.");
                throw new IllegalStateException("[SourceComponentTest] Invalid Source Component", exception);
            }

            final String svg = os.toString();
            BufferedWriter writer = new BufferedWriter(new FileWriter(DST + "/testNormalGraph.svg"));

            if (svg.contains("Syntax Error?")) {
                fail("Source component was unable to generate a valid uml syntax, test failed.");
                writer.close();
            }


            writer.write(svg);
            writer.close();

            assertEquals(plantUMLMindMap, source);
        }
    }

    @Nested
    @DisplayName("given call graph has self loop, ")
    class RecursiveCallGraph {
        private Set<String> expectedMethodSet;
        private Map<String, List<String>> expectedCallGraphMap;
        private String plantUMLMindMap;
        private List<CallGraphRelation> expectedCallGraphRelations;

        @BeforeEach
        void setUp() {
            ParsedMethodComponent parsedMethodComponent = sourceComponent.find("co.test.callGraphTest.recursion.Recursion.recurse()",
                    ParsedMethodComponent.class).orElseThrow(() -> new RuntimeException("Unable to find given method in SourceComponent."));
            methodCallGraph = new MethodCallGraphImpl(parsedMethodComponent.getAsResolvedMethodDeclaration()
                    .orElseThrow(() -> new IllegalStateException("ParsedMethodComponent does not contain ResolvedMethodDeclaration")), "co.test.callGraphTest.completeG");

            expectedMethodSet = new HashSet<>();
            expectedMethodSet.add("co.test.callGraphTest.recursion.Recursion.recurse()");

            expectedCallGraphMap = new HashMap<>();
            expectedCallGraphMap.put("co.test.callGraphTest.recursion.Recursion.recurse()", List.of("co.test.callGraphTest.recursion.Recursion.recurse()"));

            expectedCallGraphRelations =  List.of(
                    new CallGraphRelation(
                            "co.test.callGraphTest.recursion.Recursion.recurse()",
                            "co.test.callGraphTest.recursion.Recursion.recurse()"
                    ),
                    new CallGraphRelation(
                            "co.test.callGraphTest.recursion.Recursion.recurse()",
                            "co.test.callGraphTest.recursion.Recursion.recurse()"
                    )
            );

            plantUMLMindMap = "@startmindmap\n" +
                    "* <&reload> co.test.callGraphTest.recursion.Recursion.recurse()\n" +
                    "@endmindmap";
        }

        @Test
        @DisplayName("using getCallGraphRelations, should return all the relations of call graph.")
        void testGetCallGraphRelations() {
            var actualCallGraphRelations = methodCallGraph.getCallGraphRelations(new HashMap<>());
            assertThat(new HashSet<>(actualCallGraphRelations))
                    .describedAs("actual call graph relations should be same as expected call graph relations.")
                    .isEqualTo(new HashSet<>(expectedCallGraphRelations));
        }

        @Test
        @DisplayName("using getAllResolvedMethodDeclarations, should return all RMDs present in the call graph.")
        void testGetAllResolvedMethodDeclaration() {
            var allMethods = methodCallGraph.getAllResolvedMethodDeclarations();
            assertEquals(expectedMethodSet, allMethods.stream()
                    .map(ResolvedMethodDeclaration::getQualifiedSignature)
                    .collect(Collectors.toSet()));
        }

        @Test
        @DisplayName("using getCallGraph, should return root node of the call graph.")
        void testGetCallGraph() {
            var root = methodCallGraph.getCallGraph();
            var actualMethodSet = new HashSet<String>();
            extractAllMethodNames(root, actualMethodSet);
            assertEquals(expectedMethodSet, actualMethodSet);
        }

        @Test
        @DisplayName("using getCallGraphMap, should return mapping b/w Node's name -> Node's children's names.")
        void testGetCallGraphMap() {
            var actualCallGraphMap = methodCallGraph.getCallGraphMap();
            assertEquals(expectedCallGraphMap, actualCallGraphMap);
        }

        @Test
        @DisplayName("using getCallGraphString, should return mind map code for plant uml.")
        void testGetPlantUMLMindMap() throws IOException {
            String source = methodCallGraph.getPlantUMLMindMap();
            final ByteArrayOutputStream os;
            try {
                SourceStringReader reader = new SourceStringReader(source);
                os = new ByteArrayOutputStream();
                //noinspection deprecation
                reader.generateImage(os, new FileFormatOption(FileFormat.SVG));
                os.close();
            } catch (NullPointerException exception) {
                fail("Source component was unable to generate a valid uml syntax, test failed.");
                throw new IllegalStateException("[SourceComponentTest] Invalid Source Component", exception);
            }

            final String svg = os.toString();
            BufferedWriter writer = new BufferedWriter(new FileWriter(DST + "/testRecursiveGraph.svg"));

            if (svg.contains("Syntax Error?")) {
                fail("Source component was unable to generate a valid uml syntax, test failed.");
                writer.close();
            }


            writer.write(svg);
            writer.close();

            assertEquals(plantUMLMindMap, source);
        }
    }

    @Nested
    @DisplayName("given call graph is cyclic, ")
    class CyclicCallGraph {
        private Set<String> expectedMethodSet;
        private Map<String, List<String>> expectedCallGraphMap;
        private String plantUMLMindMap;
        private List<CallGraphRelation> expectedCallGraphRelations;

        @BeforeEach
        void setUp() {
            ParsedMethodComponent parsedMethodComponent = sourceComponent.find("co.test.callGraphTest.cyclicDep.ClassA.methodA()",
                    ParsedMethodComponent.class).orElseThrow(() -> new RuntimeException("Unable to find given method in SourceComponent."));
            methodCallGraph = new MethodCallGraphImpl(parsedMethodComponent.getAsResolvedMethodDeclaration()
                    .orElseThrow(() -> new IllegalStateException("ParsedMethodComponent does not contain ResolvedMethodDeclaration")), "co.test.callGraphTest.cyclicDep");

            expectedMethodSet = new HashSet<>();
            expectedMethodSet.add("co.test.callGraphTest.cyclicDep.ClassA.methodA()");
            expectedMethodSet.add("co.test.callGraphTest.cyclicDep.ClassB.methodB()");
            expectedMethodSet.add("co.test.callGraphTest.cyclicDep.ClassC.methodC()");
            expectedMethodSet.add("co.test.callGraphTest.cyclicDep.ClassD.methodD()");
            expectedMethodSet.add("co.test.callGraphTest.cyclicDep.ClassE.methodE()");

            expectedCallGraphMap = new HashMap<>();
            expectedCallGraphMap.put("co.test.callGraphTest.cyclicDep.ClassA.methodA()", List.of("co.test.callGraphTest.cyclicDep.ClassB.methodB()"));
            expectedCallGraphMap.put("co.test.callGraphTest.cyclicDep.ClassB.methodB()", List.of("co.test.callGraphTest.cyclicDep.ClassC.methodC()"));
            expectedCallGraphMap.put("co.test.callGraphTest.cyclicDep.ClassC.methodC()", List.of("co.test.callGraphTest.cyclicDep.ClassD.methodD()"));
            expectedCallGraphMap.put("co.test.callGraphTest.cyclicDep.ClassD.methodD()", List.of("co.test.callGraphTest.cyclicDep.ClassE.methodE()"));
            expectedCallGraphMap.put("co.test.callGraphTest.cyclicDep.ClassE.methodE()", List.of("co.test.callGraphTest.cyclicDep.ClassA.methodA()"));

            expectedCallGraphRelations =  List.of(
                    new CallGraphRelation(
                            "co.test.callGraphTest.cyclicDep.ClassA.methodA()",
                            "co.test.callGraphTest.cyclicDep.ClassB.methodB()"
                    ),
                    new CallGraphRelation(
                            "co.test.callGraphTest.cyclicDep.ClassB.methodB()",
                            "co.test.callGraphTest.cyclicDep.ClassC.methodC()"
                    ),
                    new CallGraphRelation(
                            "co.test.callGraphTest.cyclicDep.ClassC.methodC()",
                            "co.test.callGraphTest.cyclicDep.ClassD.methodD()"
                    ),
                    new CallGraphRelation(
                            "co.test.callGraphTest.cyclicDep.ClassD.methodD()",
                            "co.test.callGraphTest.cyclicDep.ClassE.methodE()"
                    ),
                    new CallGraphRelation(
                            "co.test.callGraphTest.cyclicDep.ClassE.methodE()",
                            "co.test.callGraphTest.cyclicDep.ClassA.methodA()"
                    )
            );

            plantUMLMindMap = "@startmindmap\n" +
                    "* co.test.callGraphTest.cyclicDep.ClassA.methodA()\n" +
                    "** co.test.callGraphTest.cyclicDep.ClassB.methodB()\n" +
                    "*** co.test.callGraphTest.cyclicDep.ClassC.methodC()\n" +
                    "**** co.test.callGraphTest.cyclicDep.ClassD.methodD()\n" +
                    "***** co.test.callGraphTest.cyclicDep.ClassE.methodE()\n" +
                    "****** co.test.callGraphTest.cyclicDep.ClassA.methodA()\n" +
                    "@endmindmap";
        }

        @Test
        @DisplayName("using getCallGraphRelations, should return all the relations of call graph.")
        void testGetCallGraphRelations() {
            var actualCallGraphRelations = methodCallGraph.getCallGraphRelations(new HashMap<>());
            assertThat(new HashSet<>(actualCallGraphRelations))
                    .describedAs("actual call graph relations should be same as expected call graph relations.")
                    .isEqualTo(new HashSet<>(expectedCallGraphRelations));
        }

        @Test
        @DisplayName("using getAllResolvedMethodDeclarations, should return all RMDs present in the call graph.")
        void testGetAllResolvedMethodDeclaration() {
            var allMethods = methodCallGraph.getAllResolvedMethodDeclarations();
            assertEquals(expectedMethodSet, allMethods.stream()
                    .map(ResolvedMethodDeclaration::getQualifiedSignature)
                    .collect(Collectors.toSet()));
        }

        @Test
        @DisplayName("using getCallGraph, should return root node of the call graph.")
        void testGetCallGraph() {
            var root = methodCallGraph.getCallGraph();
            var actualMethodSet = new HashSet<String>();
            extractAllMethodNames(root, actualMethodSet);
            assertEquals(expectedMethodSet, actualMethodSet);
        }

        @Test
        @DisplayName("using getCallGraphMap, should return mapping b/w Node's name -> Node's children's names.")
        void testGetCallGraphMap() {
            var actualCallGraphMap = methodCallGraph.getCallGraphMap();
            assertEquals(expectedCallGraphMap, actualCallGraphMap);
        }

        @Test
        @DisplayName("using getCallGraphString, should return mind map code for plant uml.")
        void testGetPlantUMLMindMap() throws IOException {
            String source = methodCallGraph.getPlantUMLMindMap();
            final ByteArrayOutputStream os;
            try {
                SourceStringReader reader = new SourceStringReader(source);
                os = new ByteArrayOutputStream();
                //noinspection deprecation
                reader.generateImage(os, new FileFormatOption(FileFormat.SVG));
                os.close();
            } catch (NullPointerException exception) {
                fail("Source component was unable to generate a valid uml syntax, test failed.");
                throw new IllegalStateException("[SourceComponentTest] Invalid Source Component", exception);
            }

            final String svg = os.toString();
            BufferedWriter writer = new BufferedWriter(new FileWriter(DST + "/testCyclicGraph.svg"));

            if (svg.contains("Syntax Error?")) {
                fail("Source component was unable to generate a valid uml syntax, test failed.");
                writer.close();
            }


            writer.write(svg);
            writer.close();

            assertEquals(plantUMLMindMap, source);
        }
    }

    @Nested
    @DisplayName("given call graph is a complete graph, ")
    class CompleteCallGraph {

        private Set<String> expectedMethodSet;
        private Map<String, List<String>> expectedCallGraphMap;
        private String plantUMLMindMap;
        private List<CallGraphRelation> expectedCallGraphRelations;

        @BeforeEach
        void setUp() {
            ParsedMethodComponent parsedMethodComponent = sourceComponent.find("co.test.callGraphTest.completeG.ClassA.methodA()",
                    ParsedMethodComponent.class).orElseThrow(() -> new RuntimeException("Unable to find given method in SourceComponent."));
            methodCallGraph = new MethodCallGraphImpl(parsedMethodComponent.getAsResolvedMethodDeclaration()
                    .orElseThrow(() -> new IllegalStateException("ParsedMethodComponent does not contain ResolvedMethodDeclaration")), "co.test.callGraphTest.completeG");

            expectedMethodSet = new HashSet<>();
            expectedMethodSet.add("co.test.callGraphTest.completeG.ClassA.methodA()");
            expectedMethodSet.add("co.test.callGraphTest.completeG.ClassB.methodB()");
            expectedMethodSet.add("co.test.callGraphTest.completeG.ClassC.methodC()");
            expectedMethodSet.add("co.test.callGraphTest.completeG.ClassD.methodD()");
            expectedMethodSet.add("co.test.callGraphTest.completeG.ClassE.methodE()");

            expectedCallGraphMap = new HashMap<>();
            expectedCallGraphMap.put("co.test.callGraphTest.completeG.ClassA.methodA()",
                    List.of("co.test.callGraphTest.completeG.ClassA.methodA()",
                            "co.test.callGraphTest.completeG.ClassB.methodB()",
                            "co.test.callGraphTest.completeG.ClassC.methodC()",
                            "co.test.callGraphTest.completeG.ClassD.methodD()",
                            "co.test.callGraphTest.completeG.ClassE.methodE()"));
            expectedCallGraphMap.put("co.test.callGraphTest.completeG.ClassB.methodB()",
                    List.of("co.test.callGraphTest.completeG.ClassA.methodA()",
                            "co.test.callGraphTest.completeG.ClassB.methodB()",
                            "co.test.callGraphTest.completeG.ClassC.methodC()",
                            "co.test.callGraphTest.completeG.ClassD.methodD()",
                            "co.test.callGraphTest.completeG.ClassE.methodE()"));
            expectedCallGraphMap.put("co.test.callGraphTest.completeG.ClassC.methodC()",
                    List.of("co.test.callGraphTest.completeG.ClassA.methodA()",
                            "co.test.callGraphTest.completeG.ClassB.methodB()",
                            "co.test.callGraphTest.completeG.ClassC.methodC()",
                            "co.test.callGraphTest.completeG.ClassD.methodD()",
                            "co.test.callGraphTest.completeG.ClassE.methodE()"));
            expectedCallGraphMap.put("co.test.callGraphTest.completeG.ClassD.methodD()",
                    List.of("co.test.callGraphTest.completeG.ClassA.methodA()",
                            "co.test.callGraphTest.completeG.ClassB.methodB()",
                            "co.test.callGraphTest.completeG.ClassC.methodC()",
                            "co.test.callGraphTest.completeG.ClassD.methodD()",
                            "co.test.callGraphTest.completeG.ClassE.methodE()"));
            expectedCallGraphMap.put("co.test.callGraphTest.completeG.ClassE.methodE()",
                    List.of("co.test.callGraphTest.completeG.ClassA.methodA()",
                            "co.test.callGraphTest.completeG.ClassB.methodB()",
                            "co.test.callGraphTest.completeG.ClassC.methodC()",
                            "co.test.callGraphTest.completeG.ClassD.methodD()",
                            "co.test.callGraphTest.completeG.ClassE.methodE()"));

            expectedCallGraphRelations =  List.of(
                    new CallGraphRelation(
                            "co.test.callGraphTest.completeG.ClassA.methodA()",
                            "co.test.callGraphTest.completeG.ClassA.methodA()"
                    ),
                    new CallGraphRelation(
                            "co.test.callGraphTest.completeG.ClassA.methodA()",
                            "co.test.callGraphTest.completeG.ClassB.methodB()"
                    ),
                    new CallGraphRelation(
                            "co.test.callGraphTest.completeG.ClassA.methodA()",
                            "co.test.callGraphTest.completeG.ClassC.methodC()"
                    ),
                    new CallGraphRelation(
                            "co.test.callGraphTest.completeG.ClassA.methodA()",
                            "co.test.callGraphTest.completeG.ClassD.methodD()"
                    ),
                    new CallGraphRelation(
                            "co.test.callGraphTest.completeG.ClassA.methodA()",
                            "co.test.callGraphTest.completeG.ClassE.methodE()"
                    ),
                    new CallGraphRelation(
                            "co.test.callGraphTest.completeG.ClassB.methodB()",
                            "co.test.callGraphTest.completeG.ClassA.methodA()"
                    ),
                    new CallGraphRelation(
                            "co.test.callGraphTest.completeG.ClassB.methodB()",
                            "co.test.callGraphTest.completeG.ClassB.methodB()"
                    ),
                    new CallGraphRelation(
                            "co.test.callGraphTest.completeG.ClassB.methodB()",
                            "co.test.callGraphTest.completeG.ClassC.methodC()"
                    ),
                    new CallGraphRelation(
                            "co.test.callGraphTest.completeG.ClassB.methodB()",
                            "co.test.callGraphTest.completeG.ClassD.methodD()"
                    ),
                    new CallGraphRelation(
                            "co.test.callGraphTest.completeG.ClassB.methodB()",
                            "co.test.callGraphTest.completeG.ClassE.methodE()"
                    ),
                    new CallGraphRelation(
                            "co.test.callGraphTest.completeG.ClassC.methodC()",
                            "co.test.callGraphTest.completeG.ClassA.methodA()"
                    ),
                    new CallGraphRelation(
                            "co.test.callGraphTest.completeG.ClassC.methodC()",
                            "co.test.callGraphTest.completeG.ClassB.methodB()"
                    ),
                    new CallGraphRelation(
                            "co.test.callGraphTest.completeG.ClassC.methodC()",
                            "co.test.callGraphTest.completeG.ClassC.methodC()"
                    ),
                    new CallGraphRelation(
                            "co.test.callGraphTest.completeG.ClassC.methodC()",
                            "co.test.callGraphTest.completeG.ClassD.methodD()"
                    ),
                    new CallGraphRelation(
                            "co.test.callGraphTest.completeG.ClassC.methodC()",
                            "co.test.callGraphTest.completeG.ClassE.methodE()"
                    ),
                    new CallGraphRelation(
                            "co.test.callGraphTest.completeG.ClassD.methodD()",
                            "co.test.callGraphTest.completeG.ClassA.methodA()"
                    ),
                    new CallGraphRelation(
                            "co.test.callGraphTest.completeG.ClassD.methodD()",
                            "co.test.callGraphTest.completeG.ClassB.methodB()"
                    ),
                    new CallGraphRelation(
                            "co.test.callGraphTest.completeG.ClassD.methodD()",
                            "co.test.callGraphTest.completeG.ClassC.methodC()"
                    ),
                    new CallGraphRelation(
                            "co.test.callGraphTest.completeG.ClassD.methodD()",
                            "co.test.callGraphTest.completeG.ClassD.methodD()"
                    ),
                    new CallGraphRelation(
                            "co.test.callGraphTest.completeG.ClassD.methodD()",
                            "co.test.callGraphTest.completeG.ClassE.methodE()"
                    ),
                    new CallGraphRelation(
                            "co.test.callGraphTest.completeG.ClassE.methodE()",
                            "co.test.callGraphTest.completeG.ClassA.methodA()"
                    ),
                    new CallGraphRelation(
                            "co.test.callGraphTest.completeG.ClassE.methodE()",
                            "co.test.callGraphTest.completeG.ClassB.methodB()"
                    ),
                    new CallGraphRelation(
                            "co.test.callGraphTest.completeG.ClassE.methodE()",
                            "co.test.callGraphTest.completeG.ClassC.methodC()"
                    ),
                    new CallGraphRelation(
                            "co.test.callGraphTest.completeG.ClassE.methodE()",
                            "co.test.callGraphTest.completeG.ClassD.methodD()"
                    ),
                    new CallGraphRelation(
                            "co.test.callGraphTest.completeG.ClassE.methodE()",
                            "co.test.callGraphTest.completeG.ClassE.methodE()"
                    )
            );

            plantUMLMindMap = "@startmindmap\n" +
                    "* <&reload> co.test.callGraphTest.completeG.ClassA.methodA()\n" +
                    "** <&reload> co.test.callGraphTest.completeG.ClassB.methodB()\n" +
                    "*** <&reload> co.test.callGraphTest.completeG.ClassA.methodA()\n" +
                    "*** <&reload> co.test.callGraphTest.completeG.ClassC.methodC()\n" +
                    "**** <&reload> co.test.callGraphTest.completeG.ClassA.methodA()\n" +
                    "**** <&reload> co.test.callGraphTest.completeG.ClassB.methodB()\n" +
                    "**** <&reload> co.test.callGraphTest.completeG.ClassD.methodD()\n" +
                    "***** <&reload> co.test.callGraphTest.completeG.ClassA.methodA()\n" +
                    "***** <&reload> co.test.callGraphTest.completeG.ClassB.methodB()\n" +
                    "***** <&reload> co.test.callGraphTest.completeG.ClassC.methodC()\n" +
                    "***** <&reload> co.test.callGraphTest.completeG.ClassE.methodE()\n" +
                    "****** <&reload> co.test.callGraphTest.completeG.ClassA.methodA()\n" +
                    "****** <&reload> co.test.callGraphTest.completeG.ClassB.methodB()\n" +
                    "****** <&reload> co.test.callGraphTest.completeG.ClassC.methodC()\n" +
                    "****** <&reload> co.test.callGraphTest.completeG.ClassD.methodD()\n" +
                    "**** <&reload> co.test.callGraphTest.completeG.ClassE.methodE()\n" +
                    "*** <&reload> co.test.callGraphTest.completeG.ClassD.methodD()\n" +
                    "*** <&reload> co.test.callGraphTest.completeG.ClassE.methodE()\n" +
                    "** <&reload> co.test.callGraphTest.completeG.ClassC.methodC()\n" +
                    "** <&reload> co.test.callGraphTest.completeG.ClassD.methodD()\n" +
                    "** <&reload> co.test.callGraphTest.completeG.ClassE.methodE()\n" +
                    "@endmindmap";
        }

        @Test
        @DisplayName("using getCallGraphRelations, should return all the relations of call graph.")
        void testGetCallGraphRelations() {
            var actualCallGraphRelations = methodCallGraph.getCallGraphRelations(new HashMap<>());
            assertThat(new HashSet<>(actualCallGraphRelations))
                    .describedAs("actual call graph relations should be same as expected call graph relations.")
                    .isEqualTo(new HashSet<>(expectedCallGraphRelations));
        }

        @Test
        @DisplayName("using getAllResolvedMethodDeclarations, should return all RMDs present in the call graph.")
        void testGetAllResolvedMethodDeclaration() {
            var allMethods = methodCallGraph.getAllResolvedMethodDeclarations();
            assertEquals(expectedMethodSet, allMethods.stream()
                    .map(ResolvedMethodDeclaration::getQualifiedSignature)
                    .collect(Collectors.toSet()));
        }

        @Test
        @DisplayName("using getCallGraph, should return root node of the call graph.")
        void testGetCallGraph() {
            var root = methodCallGraph.getCallGraph();
            var actualMethodSet = new HashSet<String>();
            extractAllMethodNames(root, actualMethodSet);
            assertEquals(expectedMethodSet, actualMethodSet);
        }

        @Test
        @DisplayName("using getCallGraphMap, should return mapping b/w Node's name -> Node's children's names.")
        void testGetCallGraphMap() {
            var actualCallGraphMap = methodCallGraph.getCallGraphMap();
            assertEquals(getEntrySet(expectedCallGraphMap), getEntrySet(actualCallGraphMap));
        }

        @Test
        @DisplayName("using getCallGraphString, should return mind map code for plant uml.")
        void testGetPlantUMLMindMap() throws IOException {
            String source = methodCallGraph.getPlantUMLMindMap();
            final ByteArrayOutputStream os;
            try {
                SourceStringReader reader = new SourceStringReader(source);
                os = new ByteArrayOutputStream();
                //noinspection deprecation
                reader.generateImage(os, new FileFormatOption(FileFormat.SVG));
                os.close();
            } catch (NullPointerException exception) {
                fail("Source component was unable to generate a valid uml syntax, test failed.");
                throw new IllegalStateException("[SourceComponentTest] Invalid Source Component", exception);
            }

            final String svg = os.toString();
            BufferedWriter writer = new BufferedWriter(new FileWriter(DST + "/testCompleteGraph.svg"));

            if (svg.contains("Syntax Error?")) {
                fail("Source component was unable to generate a valid uml syntax, test failed.");
                writer.close();
            }


            writer.write(svg);
            writer.close();

            assertEquals(plantUMLMindMap, source);
        }

        private Set<Map.Entry<String, HashSet<String>>> getEntrySet(Map<String, List<String>> set) {
            return set.entrySet()
                    .stream()
                    .map(entry -> Map.entry(entry.getKey(), new HashSet<>(entry.getValue())))
                    .collect(Collectors.toSet());
        }
    }
}
