package org.java2uml.java2umlapi.umlComponenets;

import com.github.javaparser.ast.AccessSpecifier;
import com.github.javaparser.resolution.declarations.ResolvedFieldDeclaration;
import com.github.javaparser.resolution.types.ResolvedPrimitiveType;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import org.java2uml.java2umlapi.util.umlSymbols.VisibilityModifierSymbol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for ParsedFieldComponent")
class ParsedFieldComponentTest {
    @Mock
    ResolvedFieldDeclaration resolvedFieldDeclaration;
    ParsedFieldComponent parsedFieldComponent;

    private final String fieldName = "testField";

    @BeforeEach
    void setUp() {
        assertNotNull(resolvedFieldDeclaration, "ResolvedFieldDeclaration is null.");

        doReturn(resolvedFieldDeclaration).when(resolvedFieldDeclaration).asField();
        doReturn(fieldName).when(resolvedFieldDeclaration).getName();

        parsedFieldComponent = new ParsedFieldComponent(null, resolvedFieldDeclaration);
    }

    @Test
    @DisplayName("When using getName(), parsedFieldComponent should return name.")
    void testGetName() {
        verify(resolvedFieldDeclaration, atLeastOnce()).asField();
        assertEquals(fieldName, parsedFieldComponent.getName());
        verifyNoMoreInteractions();
    }

    @Nested
    @ExtendWith(MockitoExtension.class)
    @DisplayName("When Field's type is a reference type,")
    class FieldIsReferenceTypeTest {
        @Mock
        ResolvedFieldDeclaration resolvedFieldDeclaration;


        ParsedFieldComponent parsedFieldComponent;

        private final String fieldName = "testField";
        private final String accessSpecifierStr = "private";
        private final String qualifiedName = "java.lang.String";
        private final boolean isStatic = true;

        @BeforeEach
        void setUp() {
            doReturn(resolvedFieldDeclaration).when(resolvedFieldDeclaration).asField();
            doReturn(fieldName).when(resolvedFieldDeclaration).getName();

            var resolvedReferenceType = mock(ResolvedReferenceType.class);
            doReturn(qualifiedName).when(resolvedReferenceType).getQualifiedName();

            var resolvedType = mock(ResolvedType.class);
            doReturn(true).when(resolvedType).isReferenceType();
            doReturn(resolvedReferenceType).when(resolvedType).asReferenceType();

            var accessSpecifier = mock(AccessSpecifier.class);
            doReturn(accessSpecifierStr).when(accessSpecifier).asString();

            doReturn(resolvedType).when(resolvedFieldDeclaration).getType();
            doReturn(accessSpecifier).when(resolvedFieldDeclaration).accessSpecifier();
            doReturn(isStatic).when(resolvedFieldDeclaration).isStatic();

            parsedFieldComponent = new ParsedFieldComponent(null, resolvedFieldDeclaration);
        }

        @Test
        @DisplayName("using toUML(), should return uml syntax for field.")
        void testToUML() {
            var uml = parsedFieldComponent.toUML();
            var typeStr = qualifiedName.split("\\.");

            assertTrue(uml.contains(typeStr[typeStr.length - 1]), "generated uml syntax does not contain correct type");
            assertTrue(uml.contains(fieldName), "generated uml syntax does not contain correct field name");
            assertEquals(uml.contains("static"), isStatic);
            assertTrue(uml.contains(VisibilityModifierSymbol.of(accessSpecifierStr).toString()));

            verifyNoMoreInteractions(resolvedFieldDeclaration);
        }
    }

    @Nested
    @DisplayName("When Field's type is a primitive type,")
    @ExtendWith(MockitoExtension.class)
    class FieldIsPrimitiveTypeTest {
        @Mock
        ResolvedFieldDeclaration resolvedFieldDeclaration;


        ParsedFieldComponent parsedFieldComponent;

        private final String fieldName = "testField";
        private final String accessSpecifierStr = "private";
        private final String primitiveTypeName = "INT";
        private final boolean isStatic = true;

        @BeforeEach
        void setUp() {
            doReturn(resolvedFieldDeclaration).when(resolvedFieldDeclaration).asField();
            doReturn(fieldName).when(resolvedFieldDeclaration).getName();

            var resolvedPrimitiveType = mock(ResolvedPrimitiveType.class);
            doReturn(primitiveTypeName).when(resolvedPrimitiveType).name();
            doReturn(false).when(resolvedPrimitiveType).isArray();

            var resolvedType = mock(ResolvedType.class);
            doReturn(false).when(resolvedType).isReferenceType();
            doReturn(resolvedPrimitiveType).when(resolvedType).asPrimitive();

            var accessSpecifier = mock(AccessSpecifier.class);
            doReturn(accessSpecifierStr).when(accessSpecifier).asString();

            doReturn(resolvedType).when(resolvedFieldDeclaration).getType();
            doReturn(accessSpecifier).when(resolvedFieldDeclaration).accessSpecifier();
            doReturn(isStatic).when(resolvedFieldDeclaration).isStatic();

            parsedFieldComponent = new ParsedFieldComponent(null, resolvedFieldDeclaration);
        }

        @Test
        @DisplayName("using toUML(), should return uml syntax for field.")
        void testToUML() {
            var uml = parsedFieldComponent.toUML();

            assertTrue(uml.contains(primitiveTypeName));
            assertTrue(uml.contains(fieldName));
            assertEquals(uml.contains("static"), isStatic);
            assertTrue(uml.contains(VisibilityModifierSymbol.of(accessSpecifierStr).toString()));

            verifyNoMoreInteractions(resolvedFieldDeclaration);
        }
    }
}