package org.java2uml.java2umlapi.parser;

import com.github.javaparser.ast.Node;

import java.util.Map;

/**
 * <p>
 * Facade for java parser library, used for parsing java source files.
 * </p>
 *
 * @author kawaiifox.
 */
public class JavaParserFacade {

    /**
     * <p>
     * parses, the source files at given path and returns a mapping between a:String -> b:Node.
     * where, a is  fully-qualified class name and b is the corresponding class.
     * </p>
     *
     * @param PATH - path of the source directory to be parsed.
     * @return Map<String, Node> - Mapping between className -> Node.
     */
    public Map<String, Node> parse(String PATH) {

        return null;
    }
}
