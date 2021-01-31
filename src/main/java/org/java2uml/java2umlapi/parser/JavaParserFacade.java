package org.java2uml.java2umlapi.parser;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.java2uml.java2umlapi.util.DirExplorer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Facade for java parser library, used for parsing java source files.
 * </p>
 *
 * @author kawaiifox.
 */
@Component
public class JavaParserFacade {

    private final DirExplorer dirExplorer;

    @Autowired
    public JavaParserFacade(DirExplorer dirExplorer) {
        this.dirExplorer = dirExplorer;
    }

    /**
     * <p>
     * parses, the source files at given path and returns a mapping between a:String -> b:Node.
     * where, a is  fully-qualified class name and b is the corresponding class.
     * </p>
     *
     * @param PATH - path of the source directory to be parsed.
     * @return Map<String, ClassOrInterfaceDeclaration> - Mapping between className -> Node.
     */
    public Map<String, ClassOrInterfaceDeclaration> parseClasses(String PATH) {
        Map<String, ClassOrInterfaceDeclaration> classNameToclassOrInterfaceDeclMap = new HashMap<>();

        dirExplorer.explore(new File(PATH), classNameToclassOrInterfaceDeclMap);

        return classNameToclassOrInterfaceDeclMap;
    }
}
