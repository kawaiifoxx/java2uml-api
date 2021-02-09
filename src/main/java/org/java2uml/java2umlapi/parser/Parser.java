package org.java2uml.java2umlapi.parser;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedDeclaration;
import com.github.javaparser.symbolsolver.utils.SymbolSolverCollectionStrategy;
import com.github.javaparser.utils.ProjectRoot;
import com.github.javaparser.utils.SourceRoot;
import org.java2uml.java2umlapi.umlComponenets.SourceComponent;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Facade for java parser library and java symbol solver, used for parsing java source files.
 * </p>
 *
 * @author kawaiifox.
 */
public class Parser {
    /**
     * <p>
     * resolves all reference types in given java source directory and returns a SourceComponent.
     * </p>
     *
     * @param PATH path to the source directory to be parsed.
     * @return returns SourceComponent instance for corresponding java source directory.
     * @throws RuntimeException if there is no .java files in given directory or its subdirectories.
     */
    public static SourceComponent parse(Path PATH) {
        ProjectRoot projectRoot = new SymbolSolverCollectionStrategy().collect(PATH);

        var sourceRoots = projectRoot.getSourceRoots();

        if (sourceRoots.isEmpty()) {
            throw new RuntimeException("[Parser] Source directory is empty i.e it does not contain any .java or .jar files.");
        }

        List<ResolvedDeclaration> resolvedDeclarations = getResolvedDeclarations(sourceRoots);

        return new SourceComponent(resolvedDeclarations);
    }

    /**
     * @param sourceRoots List of SourceRoot.
     * @return return a List<ResolvedDeclaration>.
     * @throws RuntimeException if passed sourceRoots is empty.
     */
    @NotNull
    private static List<ResolvedDeclaration> getResolvedDeclarations(List<SourceRoot> sourceRoots) {
        var classOrInterfaceDeclarations = getClassOrInterfaceDeclarations(sourceRoots);

        if (sourceRoots.get(0).getParserConfiguration().getSymbolResolver().isEmpty()) {
            throw new RuntimeException("[Parser] Unable to get symbolResolver.");
        }

        var symbolResolver = sourceRoots.get(0).getParserConfiguration().getSymbolResolver().get();
        List<ResolvedDeclaration> resolvedDeclarations = new ArrayList<>();
        classOrInterfaceDeclarations
                .forEach(classOrInterfaceDeclaration -> resolvedDeclarations
                        .add(symbolResolver
                                .resolveDeclaration(classOrInterfaceDeclaration, ResolvedDeclaration.class)));
        return resolvedDeclarations;
    }

    /**
     * Uses visitor to explore each compilation unit and retrieve every ClassOrInterfaceDeclaration from it.
     * @param sourceRoots list of sourceRoot, containing information about projects.
     *                    each sourceRoot contains information about one project.
     * @return Returns a list of classOrInterfaceDeclarations
     */
    @NotNull
    private  static List<ClassOrInterfaceDeclaration> getClassOrInterfaceDeclarations(List<SourceRoot> sourceRoots) {
        var compilationUnits = getAllCompilationUnits(sourceRoots);
        List<ClassOrInterfaceDeclaration> classOrInterfaceDeclarations = new ArrayList<>();

        VoidVisitorAdapter<List<ClassOrInterfaceDeclaration>> visitor = new VoidVisitorAdapter<>() {
            @Override
            public void visit(ClassOrInterfaceDeclaration n, List<ClassOrInterfaceDeclaration> arg) {
                super.visit(n, arg);
                arg.add(n);
            }
        };

        compilationUnits.forEach(compilationUnit -> visitor.visit(compilationUnit, classOrInterfaceDeclarations));
        return classOrInterfaceDeclarations;
    }


    /**
     * Takes a list of SourceRoot and try to parse parallely each .java file on success add them to list of compilationUnit.
     * Unsuccessful parse results are ignored.
     * @param sourceRoots List of SourceRoot.
     * @return Returns all the compilation units from the source directory.
     */
    private static List<CompilationUnit> getAllCompilationUnits(List<SourceRoot> sourceRoots) {
        List<CompilationUnit> compilationUnits = new ArrayList<>();

        sourceRoots.forEach(sourceRoot -> {
            var parseResults = sourceRoot.tryToParseParallelized();
            parseResults.forEach(parseResult -> {
                if (parseResult.isSuccessful()) {
                    //noinspection OptionalGetWithoutIsPresent
                    compilationUnits.add(parseResult.getResult().get());
                }
            });
        });

        return compilationUnits;
    }
}
