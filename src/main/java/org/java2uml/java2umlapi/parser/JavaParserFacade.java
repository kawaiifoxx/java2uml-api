package org.java2uml.java2umlapi.parser;

import com.github.javaparser.ast.Node;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;

import java.io.FileNotFoundException;
import java.util.Map;

/**
 * Facade for java parser library, used for parsing java source files.
 *
 * @author kawaiifox.
 */
public class JavaParserFacade {

    /**
     * parses, the source files at given path and returns a mapping between a:String -> b:Node.
     * where, a is  fully-qualified class name and b is the corresponding class.
     * @param PATH - path of the source directory to be parsed.
     * @return Map<String, Node> - Mapping between className -> Node.
     */
    public Map<String, Node> parse(String PATH)  {

        return null;
    }
}
