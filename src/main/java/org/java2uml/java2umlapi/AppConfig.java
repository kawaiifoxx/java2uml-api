package org.java2uml.java2umlapi;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitor;
import org.java2uml.java2umlapi.parser.ClassOrInterfaceCollector;
import org.java2uml.java2umlapi.util.DirExplorer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.FileNotFoundException;
import java.util.Map;

@Configuration
public class AppConfig {

    @Bean
    public DirExplorer dirExplorer(@Autowired VoidVisitor<Map<String, ClassOrInterfaceDeclaration>> visitor) {
        return new DirExplorer((level, path, file) -> file.getName().endsWith(".java"), (level, path, file, state) -> {
            try {
                CompilationUnit cu = StaticJavaParser.parse(file);
                visitor.visit(cu, state);
            } catch (FileNotFoundException e) {
                throw new RuntimeException("[DirExplorer.FileHandler] File not found at: " + file.getAbsolutePath());
            }
        });
    }
}
