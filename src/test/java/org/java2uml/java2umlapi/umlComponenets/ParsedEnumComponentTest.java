package org.java2uml.java2umlapi.umlComponenets;

import com.github.javaparser.resolution.declarations.ResolvedEnumDeclaration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
@DisplayName("When using ParsedEnumComponent,")
class ParsedEnumComponentTest {

    private ParsedEnumComponent parsedComponent;
    private final String fullyQualifiedEnumName = "co.test.EnumTest";

    private final String child1UML = "ENUM_CONST1";
    private final String child2UML = "ENUM_CONST2";
    private final String child3UML = "ENUM_CONST3";
    private final String child4UML = "ENUM_CONST4";
    private final String child5UML = "+ {method} [Constructor] EnumTest()";
    private final String child6UML = "- String testField1";
    private final String child7UML = "- String testField2";
    private final String child8UML = "- INT testField3";
    private final String child9UML = "+ getTestField1(): String";
    private final String child10UML = "+ getTestField2(): String";


    @BeforeEach
    void setUp() {
        var resolvedEnumDeclaration = mock(ResolvedEnumDeclaration.class);
        doReturn(fullyQualifiedEnumName).when(resolvedEnumDeclaration).getQualifiedName();

        var child1 = mock(ParsedComponent.class);
        lenient().doReturn("child1").when(child1).getName();
        lenient().doReturn(true).when(child1).isParsedEnumConstantComponent();
        lenient().doReturn(child1UML).when(child1).toUML();

        var child2 = mock(ParsedComponent.class);
        lenient().doReturn("child2").when(child2).getName();
        lenient().doReturn(true).when(child2).isParsedEnumConstantComponent();
        lenient().doReturn(child2UML).when(child2).toUML();

        var child3 = mock(ParsedComponent.class);
        lenient().doReturn("child3").when(child3).getName();
        lenient().doReturn(true).when(child3).isParsedEnumConstantComponent();
        lenient().doReturn(child3UML).when(child3).toUML();

        var child4 = mock(ParsedComponent.class);
        lenient().doReturn("child4").when(child4).getName();
        lenient().doReturn(true).when(child4).isParsedEnumConstantComponent();
        lenient().doReturn(child4UML).when(child4).toUML();

        var child5 = mock(ParsedComponent.class);
        lenient().doReturn("child5").when(child5).getName();
        lenient().doReturn(true).when(child5).isParsedConstructorComponent();
        lenient().doReturn(child5UML).when(child5).toUML();

        var child6 = mock(ParsedComponent.class);
        lenient().doReturn("child6").when(child6).getName();
        lenient().doReturn(true).when(child6).isParsedFieldComponent();
        lenient().doReturn(child6UML).when(child6).toUML();

        var child7 = mock(ParsedComponent.class);
        lenient().doReturn("child7").when(child7).getName();
        lenient().doReturn(true).when(child7).isParsedFieldComponent();
        lenient().doReturn(child7UML).when(child7).toUML();

        var child8 = mock(ParsedComponent.class);
        lenient().doReturn("child8").when(child8).getName();
        lenient().doReturn(true).when(child8).isParsedFieldComponent();
        lenient().doReturn(child8UML).when(child8).toUML();

        var child9 = mock(ParsedComponent.class);
        lenient().doReturn("child9").when(child9).getName();
        lenient().doReturn(true).when(child9).isParsedMethodComponent();
        lenient().doReturn(child9UML).when(child9).toUML();

        var child10 = mock(ParsedComponent.class);
        lenient().doReturn("child10").when(child10).getName();
        lenient().doReturn(true).when(child10).isParsedMethodComponent();
        lenient().doReturn(child10UML).when(child10).toUML();

        parsedComponent = new ParsedEnumComponent(resolvedEnumDeclaration, mock(ParsedComponent.class));

        parsedComponent.addChild(child1);
        parsedComponent.addChild(child2);
        parsedComponent.addChild(child3);
        parsedComponent.addChild(child4);
        parsedComponent.addChild(child5);
        parsedComponent.addChild(child6);
        parsedComponent.addChild(child7);
        parsedComponent.addChild(child8);
        parsedComponent.addChild(child9);
        parsedComponent.addChild(child10);

    }

    @Test
    @DisplayName("using getName, should return fully qualified name of the Enum class.")
    void testGetName() {
        assertEquals(fullyQualifiedEnumName, parsedComponent.getName());
    }

    @Test
    @DisplayName("using toUML, should return uml syntax with all its children elements.")
    void testToUML() {
        var uml = parsedComponent.toUML();
        var children = parsedComponent.getChildren();
        children.forEach((k,v) -> assertTrue(uml.contains(v.toUML())));
    }
}