package org.java2uml.java2umlapi.umlComponenets;

import com.github.javaparser.ast.AccessSpecifier;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
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
@DisplayName("When using ParsedMethodComponent,")
class ParsedMethodComponentTest {

    private ParsedMethodComponent parsedComponent;

    private final String qualifiedName = "co.test.Test.testMethod()";
    private final String umlName = "testMethod()";
    private final String qualifiedReturnType = "java.lang.String";
    private final String accessSpecifierStr = "public";
    private final boolean isStatic = false;

    @BeforeEach
    void setUp() {
        var resolvedDeclaration = mock(ResolvedMethodDeclaration.class);
        var resolvedReferenceType = mock(ResolvedReferenceType.class);
        lenient().doReturn(qualifiedReturnType).when(resolvedReferenceType).getQualifiedName();

        var accessSpecifier = mock(AccessSpecifier.class);
        lenient().doReturn(accessSpecifierStr).when(accessSpecifier).asString();

        var resolvedType = mock(ResolvedType.class);
        lenient().doReturn(false).when(resolvedType).isVoid();
        lenient().doReturn(true).when(resolvedType).isReferenceType();
        lenient().doReturn(resolvedReferenceType).when(resolvedType).asReferenceType();

        doReturn(qualifiedName).when(resolvedDeclaration).getQualifiedSignature();
        doReturn(umlName).when(resolvedDeclaration).getSignature();
        lenient().doReturn(resolvedType).when(resolvedDeclaration).getReturnType();
        lenient().doReturn(accessSpecifier).when(resolvedDeclaration).accessSpecifier();
        lenient().doReturn(isStatic).when(resolvedDeclaration).isStatic();

        parsedComponent = new ParsedMethodComponent(mock(ParsedClassOrInterfaceComponent.class), resolvedDeclaration);
    }

    @Test
    @DisplayName("using getName, should return qualified method signature.")
    void testGetName() {
        assertEquals(qualifiedName, parsedComponent.getName());
    }

    @Test
    @DisplayName("using getReturnType, should return qualified return type.")
    void testGetReturnTypeName() {
        var returnType = qualifiedReturnType.split("\\.");
        assertEquals(returnType[returnType.length - 1], parsedComponent.getReturnTypeName(), "incorrect return type.");
    }

    @Test
    @DisplayName("using umlExtractor on this component, should return uml syntax for method component.")
    void testToUML() {
        var uml = parsedComponent.accept(new UMLExtractor());

        assertTrue(uml.contains(VisibilityModifierSymbol.of(accessSpecifierStr).toString())
                , "generated uml does not contain correct accessSpecifier");
        assertEquals(isStatic, uml.contains("static"));
        assertTrue(uml.contains(umlName), "generated uml does not contain correct name");
        var returnType = qualifiedReturnType.split("\\.");
        assertTrue(uml.contains(returnType[returnType.length - 1]), "generated uml does not contain correct return type.");

    }
}