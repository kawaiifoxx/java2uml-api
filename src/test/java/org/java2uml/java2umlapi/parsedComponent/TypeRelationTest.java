package org.java2uml.java2umlapi.parsedComponent;

import org.java2uml.java2umlapi.util.umlSymbols.RelationsSymbol;
import org.java2uml.java2umlapi.visitors.umlExtractor.UMLExtractor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@DisplayName("When using TypeRelation,")
class TypeRelationTest {

    private TypeRelation typeRelation;

    private final String relationType = RelationsSymbol.ASSOCIATION_AR.toString();
    private final String fromTypeName = "co.test.Test1";
    private final String toTypeName = "co.test.Test2";

    @BeforeEach
    void setUp() {
        var from = mock(ParsedCompositeComponent.class);
        doReturn(fromTypeName).when(from).getName();

        var to = mock(ParsedCompositeComponent.class);
        doReturn(toTypeName).when(to).getName();

        typeRelation = new TypeRelation(from, to, relationType, RelationsSymbol.ASSOCIATION_AR);
    }

    @Test
    @DisplayName("using umlExtractor on this component, should return correct uml syntax.")
    void testToUML() {
        var uml = typeRelation.accept(new UMLExtractor());

        assertTrue(uml.contains(fromTypeName), "generated uml does not contain correct fromTypeName");
        assertTrue(uml.contains(toTypeName), "generated uml does not contain correct toTypeName");
        assertTrue(uml.contains(relationType), "generated uml does not contain correct relation type.");

    }
}