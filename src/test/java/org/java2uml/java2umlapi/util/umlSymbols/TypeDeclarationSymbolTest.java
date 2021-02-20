package org.java2uml.java2umlapi.util.umlSymbols;

import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedTypeDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedTypeParameterDeclaration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@DisplayName("When using TypeDeclarationSymbol,")
@ExtendWith(MockitoExtension.class)
class TypeDeclarationSymbolTest {
    private final ResolvedTypeDeclaration resolvedDeclaration = mock(ResolvedTypeDeclaration.class);

    @BeforeEach
    void setUp() {
        var resolvedReferenceTypeDeclaration = mock(ResolvedReferenceTypeDeclaration.class);
        doReturn(true).when(resolvedReferenceTypeDeclaration).isGeneric();

        var  typeParam1 = mock(ResolvedTypeParameterDeclaration.class);
        doReturn("S").when(typeParam1).getName();
        var  typeParam2 = mock(ResolvedTypeParameterDeclaration.class);
        doReturn("T").when(typeParam2).getName();
        var  typeParam3 = mock(ResolvedTypeParameterDeclaration.class);
        doReturn("U").when(typeParam3).getName();
        var  typeParam4 = mock(ResolvedTypeParameterDeclaration.class);
        doReturn("V").when(typeParam4).getName();
        var  typeParam5 = mock(ResolvedTypeParameterDeclaration.class);
        doReturn("R").when(typeParam5).getName();

        doReturn(List.of(typeParam1, typeParam2, typeParam3, typeParam4, typeParam5))
                .when(resolvedReferenceTypeDeclaration).getTypeParameters();

        doReturn(true).when(resolvedDeclaration).isClass();
        doReturn("co.test.TestClass").when(resolvedDeclaration).getQualifiedName();
        doReturn(resolvedReferenceTypeDeclaration).when(resolvedDeclaration).asReferenceType();
    }

    @DisplayName("using getTypeDeclarationSymbol should return fully qualified class name " +
            "with \"class\" or \"interface\" and full list of type params if resolvedTypeDeclaration is generic.")
    @Test
    void testGetTypeDeclarationSymbol() {
        String expectedTypeDeclarationSymbol = "class co.test.TestClass <S, T, U, V, R>";
        assertEquals(expectedTypeDeclarationSymbol, TypeDeclarationSymbol.getTypeDeclarationSymbol(resolvedDeclaration));
    }
}