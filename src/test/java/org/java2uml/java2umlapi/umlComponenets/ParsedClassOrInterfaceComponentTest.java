package org.java2uml.java2umlapi.umlComponenets;

import com.github.javaparser.resolution.declarations.ResolvedDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedTypeDeclaration;
import org.java2uml.java2umlapi.visitors.umlExtractor.UMLExtractor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("When using ParsedClassOrInterfaceComponent,")
class ParsedClassOrInterfaceComponentTest {

    private ParsedClassOrInterfaceComponent parsedComponent;
    private final String fullyQualifiedClassOrInterfaceName = "co.test.ClassOrInterfaceTest";

    private final String child1UML = "- String testField1";
    private final String child2UML = "- int testField2";
    private final String child3UML = "+ {method} [Constructor] ClassOrInterfaceTest()";
    private final String child4UML = "+ {method} getTestField1(): String";
    private final String child5UML = "+ {method} getTestField2(): int";

    @BeforeEach
    void setUp() {
        var resolvedReferenceTypeDeclaration = mock(ResolvedReferenceTypeDeclaration.class);
        lenient().doReturn(true).when(resolvedReferenceTypeDeclaration).isClass();

        var resolvedTypeDeclaration = mock(ResolvedTypeDeclaration.class);
        doReturn(fullyQualifiedClassOrInterfaceName).when(resolvedTypeDeclaration).getQualifiedName();
        lenient().doReturn(resolvedReferenceTypeDeclaration).when(resolvedTypeDeclaration).asReferenceType();

        var resolvedDeclaration = mock(ResolvedDeclaration.class);
        doReturn(resolvedTypeDeclaration).when(resolvedDeclaration).asType();

        var parsedFieldComponent1 = mock(ParsedFieldComponent.class);
        lenient().doReturn(true).when(parsedFieldComponent1).isParsedFieldComponent();
        lenient().doReturn("child1").when(parsedFieldComponent1).getName();
        lenient().doReturn(child1UML).when(parsedFieldComponent1).accept(any(UMLExtractor.class));

        var parsedFieldComponent2 = mock(ParsedFieldComponent.class);
        lenient().doReturn(true).when(parsedFieldComponent2).isParsedFieldComponent();
        lenient().doReturn("child2").when(parsedFieldComponent2).getName();
        lenient().doReturn(child2UML).when(parsedFieldComponent2).accept(any(UMLExtractor.class));

        var parsedConstructorComponent = mock(ParsedConstructorComponent.class);
        lenient().doReturn(true).when(parsedConstructorComponent).isParsedConstructorComponent();
        lenient().doReturn("child3").when(parsedConstructorComponent).getName();
        lenient().doReturn(child3UML).when(parsedConstructorComponent).accept(any(UMLExtractor.class));

        var parsedMethodComponent1 = mock(ParsedMethodComponent.class);
        lenient().doReturn(true).when(parsedMethodComponent1).isParsedMethodComponent();
        lenient().doReturn("child4").when(parsedMethodComponent1).getName();
        lenient().doReturn(child4UML).when(parsedMethodComponent1).accept(any(UMLExtractor.class));

        var parsedMethodComponent2 = mock(ParsedMethodComponent.class);
        lenient().doReturn(true).when(parsedMethodComponent2).isParsedMethodComponent();
        lenient().doReturn("child5").when(parsedMethodComponent2).getName();
        lenient().doReturn(child5UML).when(parsedMethodComponent2).accept(any(UMLExtractor.class));

        parsedComponent = new ParsedClassOrInterfaceComponent(resolvedDeclaration, mock(ParsedComponent.class));

        parsedComponent.addChild(parsedFieldComponent1);
        parsedComponent.addChild(parsedFieldComponent2);
        parsedComponent.addChild(parsedConstructorComponent);
        parsedComponent.addChild(parsedMethodComponent1);
        parsedComponent.addChild(parsedMethodComponent2);
    }

    @DisplayName("using getName, should return the fullyQualified name of ParsedClassOrInterfaceComponent")
    @Test
    void testGetName() {
        assertEquals(fullyQualifiedClassOrInterfaceName, parsedComponent.getName());
    }

    @DisplayName("using umlExtractor on this component, should return" +
            " the uml representation for ParsedClassOrInterfaceComponent")
    @Test
    void testToUML() {
        var uml = parsedComponent.accept(new UMLExtractor());

        assertTrue(uml.contains("class"));
        assertTrue(uml.contains(fullyQualifiedClassOrInterfaceName));
        assertTrue(uml.contains(child1UML));
        assertTrue(uml.contains(child2UML));
        assertTrue(uml.contains(child3UML));
        assertTrue(uml.contains(child4UML));
        assertTrue(uml.contains(child5UML));
    }
}