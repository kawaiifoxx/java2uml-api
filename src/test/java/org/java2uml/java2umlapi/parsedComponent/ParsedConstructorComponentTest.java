package org.java2uml.java2umlapi.parsedComponent;

import com.github.javaparser.ast.AccessSpecifier;
import com.github.javaparser.resolution.declarations.ResolvedConstructorDeclaration;
import org.java2uml.java2umlapi.util.umlSymbols.VisibilityModifierSymbol;
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
@DisplayName("When using ParsedConstructorComponent, ")
class ParsedConstructorComponentTest {


    private ParsedConstructorComponent parsedComponent;

    private final String name = "TestConstructor()";
    private final String accessSpecifierStr = "public";

    @BeforeEach
    void setUp() {
        var resolvedDeclaration = mock(ResolvedConstructorDeclaration.class);
        lenient().doReturn(name).when(resolvedDeclaration).getSignature();
        doReturn(name).when(resolvedDeclaration).getQualifiedSignature();

        var accessSpecifier = mock(AccessSpecifier.class);
        lenient().doReturn(accessSpecifierStr).when(accessSpecifier).asString();

        lenient().doReturn(accessSpecifier).when(resolvedDeclaration).accessSpecifier();

        parsedComponent = new ParsedConstructorComponent(mock(ParsedClassOrInterfaceComponent.class), resolvedDeclaration);
    }

    @Test
    @DisplayName("using getName, should return constructor's signature.")
    void testGetName() {
        assertEquals(name, parsedComponent.getName());
    }

    @Test
    @DisplayName("using umlExtractor on this component, " +
            "should return UML syntax for the constructor.")
    void testToUML() {
        var uml = parsedComponent.accept(new UMLExtractor());

        assertTrue(uml.contains(name));
        assertTrue(uml.contains(VisibilityModifierSymbol
                .of(accessSpecifierStr)
                .toString()));
    }
}