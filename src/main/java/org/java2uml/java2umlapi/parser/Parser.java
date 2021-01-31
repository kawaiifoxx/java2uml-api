package org.java2uml.java2umlapi.parser;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import org.java2uml.java2umlapi.util.DirExplorer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
     * parses, the source files at given path and returns a mapping between a:String -> b:ClassOrInterfaceDeclaration.
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
     * resolves all reference types in given java source directory and adds to a map.
     * </p>
     *
     * @param PATH path to the source directory to be parsed.
     * @return returns mapping between a:String -> b:ResolvedReferenceTypeDeclaration
     */
    public List<ResolvedReferenceTypeDeclaration> getAllResolvedReferenceTypes(String PATH) {

        var classList = parseClasses(PATH);
        TypeSolver typeSolver = new JavaParserTypeSolver(PATH);
        List<ResolvedReferenceTypeDeclaration> resolvedReferenceTypeDeclarationList = new ArrayList<>();

        classList.forEach(k -> resolvedReferenceTypeDeclarationList.add(typeSolver.solveType(k)));

        return resolvedReferenceTypeDeclarationList;
    }
}
