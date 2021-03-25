package org.java2uml.java2umlapi.parsedComponent.parsedMethodComponentTests;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import org.java2uml.java2umlapi.parsedComponent.ParsedMethodComponent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("ParsedComponent")
@DisplayName("When return type of a method is array type,")
public class ParsedMethodComponentReturnTypeTest {
    @Test
    @DisplayName("using ParsedMethodComponent.getReturnType should return the return type with [].")
    void test() {
        var config = new ParserConfiguration();
        config.setSymbolResolver(new JavaSymbolSolver(new ReflectionTypeSolver(false)));
        var code = "public class SpringMvcDispatcherServletInitializer {\n" +
                "    \n" +
                "    protected Class<?>[] getRootConfigClasses() {\n" +
                "        return null;\n" +
                "    }\n" +
                "    \n" +
                "    protected Class<?>[] getServletConfigClasses() {\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    protected String[] getServletMappings() {\n" +
                "        return new String[]{\"/\"};\n" +
                "    }\n" +
                "}";

        StaticJavaParser.setConfiguration(config);
        CompilationUnit cu = StaticJavaParser.parse(code);
        var returnTypeList = cu.findAll(MethodDeclaration.class)
                .stream()
                .map(MethodDeclaration::resolve)
                .map(resolvedMethodDeclaration -> new ParsedMethodComponent(null, resolvedMethodDeclaration))
                .map(ParsedMethodComponent::getReturnTypeName)
                .collect(Collectors.toList());

        assertThat(returnTypeList).allMatch(returnType -> returnType.contains("[]"));
    }
}
