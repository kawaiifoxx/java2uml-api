package org.java2uml.java2umlapi.umlComponenets;

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
@DisplayName("When using ParsedExternalComponent,")
class ParsedExternalComponentTest {

    ParsedExternalComponent parsedComponent;

    private final String fullyQualifiedName = "co.test.Test";
    private final boolean isClass = true;
    private final boolean isInterface = false;

    @BeforeEach
    void setUp() {
        var resolvedReferenceType = mock(ResolvedReferenceTypeDeclaration.class);
        lenient().doReturn(false).when(resolvedReferenceType).isGeneric();

        var resolvedDeclaration = mock(ResolvedTypeDeclaration.class);
        doReturn(fullyQualifiedName).when(resolvedDeclaration).getQualifiedName();
        lenient().doReturn(isClass).when(resolvedDeclaration).isClass();
        lenient().doReturn(isInterface).when(resolvedDeclaration).isInterface();
        lenient().doReturn(resolvedReferenceType).when(resolvedDeclaration).asReferenceType();
        parsedComponent = new ParsedExternalComponent(resolvedDeclaration);
    }

    @Test
    @DisplayName("using getName, should return name of external component.")
    void testGetName() {
        assertEquals(fullyQualifiedName, parsedComponent.getName(), "returned name is incorrect.");
    }

    @Test
    @DisplayName("using umlExtractor on this component, should return correct uml syntax for ParsedExternalComponent.")
    void testToUML() {
        var uml = parsedComponent.accept(new UMLExtractor());

        assertTrue(uml.contains(fullyQualifiedName));
        assertEquals(isClass, uml.contains("class"));
        assertEquals(isInterface, uml.contains("interface"));
    }
}