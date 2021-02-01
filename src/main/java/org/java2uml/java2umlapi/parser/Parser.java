package org.java2uml.java2umlapi.parser;

import com.github.javaparser.resolution.declarations.ResolvedDeclaration;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import org.java2uml.java2umlapi.util.DirExplorer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Facade for java parser library, used for parsing java source files.
 * </p>
 *
 * @author kawaiifox.
 */
@Component
public class Parser {

    private final DirExplorer dirExplorer;

    @Autowired
    public Parser(DirExplorer dirExplorer) {
        this.dirExplorer = dirExplorer;
    }

    /**
     * <p>
     * parses, the source files at given path and returns a list of fully qualified names of all the classes or interfaces.
     * where, a is  fully-qualified class name and b is the corresponding class.
     * </p>
     *
     * @param PATH path of the source directory to be parsed.
     * @return returns List of all QualifiedClassNames.
     */
    public List<String> parseClasses(String PATH) {
        List<String> classNameList = new ArrayList<>();

        dirExplorer.explore(new File(PATH), classNameList);

        return classNameList;
    }

    /**
     * <p>
     * resolves all reference types in given java source directory and adds to a list.
     * </p>
     *
     * @param PATH path to the source directory to be parsed.
     * @return returns list of all reference types.
     */
    public List<ResolvedDeclaration> getAllResolvedDeclarations(String PATH) {

        var classList = parseClasses(PATH);
        TypeSolver typeSolver = new JavaParserTypeSolver(PATH);
        List<ResolvedDeclaration> resolvedDeclarationList = new ArrayList<>();

        classList.forEach(k -> resolvedDeclarationList.add(typeSolver.solveType(k)));

        return resolvedDeclarationList;
    }
}
