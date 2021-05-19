package org.java2uml.java2umlapi.parser;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedDeclaration;
import com.github.javaparser.symbolsolver.utils.SymbolSolverCollectionStrategy;
import com.github.javaparser.utils.ProjectRoot;
import com.github.javaparser.utils.SourceRoot;
import org.java2uml.java2umlapi.exceptions.EmptySourceDirectoryException;
import org.java2uml.java2umlapi.parsedComponent.SourceComponent;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * Facade for java parser library and java symbol solver, used for parsing java source files.
 * </p>
 *
 * <p>
 *     Parser will not parse files in hidden directory or any files in folders starting with <code>'.'</code>
 *     For eg <code>.hidden</code>
 * </p>
 *
 * @author kawaiifox.
 */
public abstract class Parser {
    /**
     * <p>
     * resolves all reference types in given java source directory and returns a SourceComponent.
     * </p>
     *
     * @param PATH path to the source directory to be parsed.
     * @return returns SourceComponent instance for corresponding java source directory.
     * @throws EmptySourceDirectoryException if there is no .java files in given directory or its subdirectories.
     */
    public static SourceComponent parse(Path PATH) throws EmptySourceDirectoryException {
        ProjectRoot projectRoot = new SymbolSolverCollectionStrategy().collect(PATH);

        var sourceRoots = projectRoot.getSourceRoots();

        if (sourceRoots.isEmpty()) {
            throw new EmptySourceDirectoryException(
                    "Source directory is empty i.e it does not contain any .java or .jar files."
            );
        }

        List<ResolvedDeclaration> resolvedDeclarations = getResolvedDeclarations(sourceRoots);

        return new SourceComponent(resolvedDeclarations);
    }

    /**
     * @param sourceRoots List of SourceRoot.
     * @return return a List<ResolvedDeclaration>.
     * @throws RuntimeException if passed sourceRoots is empty.
     */
    private static List<ResolvedDeclaration> getResolvedDeclarations(List<SourceRoot> sourceRoots) {
        var compilationUnits = getAllCompilationUnits(sourceRoots);
        var classOrInterfaceDeclarations = getClassOrInterfaceDeclarations(compilationUnits);
        var enumDecl = getEnumDeclaration(compilationUnits);
        var symbolResolver = sourceRoots.get(0).getParserConfiguration().getSymbolResolver()
                .orElseThrow(() -> new RuntimeException("[Parser] Unable to get symbolResolver."));

        List<ResolvedDeclaration> resolvedDeclarations = new ArrayList<>();
        classOrInterfaceDeclarations
                .forEach(classOrInterfaceDeclaration -> resolvedDeclarations
                        .add(symbolResolver
                                .resolveDeclaration(classOrInterfaceDeclaration, ResolvedDeclaration.class)));

        enumDecl
                .forEach(enumDeclaration -> resolvedDeclarations
                        .add(symbolResolver
                                .resolveDeclaration(enumDeclaration, ResolvedDeclaration.class)));
        return resolvedDeclarations;
    }

    /**
     * Uses visitor to explore each compilation unit,  and then return these declarations.
     *
     * @param compilationUnits List of compilation units
     * @return Returns a list of EnumDeclaration.
     */
    private static List<EnumDeclaration> getEnumDeclaration(List<CompilationUnit> compilationUnits) {
        List<EnumDeclaration> enumDeclarations = new ArrayList<>();

        VoidVisitorAdapter<List<EnumDeclaration>> visitor = new VoidVisitorAdapter<>() {
            @Override
            public void visit(EnumDeclaration n, List<EnumDeclaration> arg) {
                super.visit(n, arg);
                arg.add(n);
            }
        };

        compilationUnits.forEach(compilationUnit -> visitor.visit(compilationUnit, enumDeclarations));
        return enumDeclarations;
    }

    /**
     * Uses visitor to explore each compilation unit and retrieve every ClassOrInterfaceDeclaration from it.
     *
     * @return Returns a list of classOrInterfaceDeclarations
     */
    private static List<ClassOrInterfaceDeclaration> getClassOrInterfaceDeclarations(List<CompilationUnit> compilationUnits) {
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
     * Takes a list of SourceRoot and try to parse parallelly each .java file on success add them to list of compilationUnit.
     * Unsuccessful parse results are ignored.
     *
     * @param sourceRoots List of SourceRoot.
     * @return Returns all the compilation units from the source directory.
     */
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private static List<CompilationUnit> getAllCompilationUnits(List<SourceRoot> sourceRoots) {
        List<CompilationUnit> compilationUnits = new ArrayList<>();

        sourceRoots.forEach(sourceRoot -> {
            var parseResults = sourceRoot.tryToParseParallelized();
            parseResults.forEach(parseResult -> {
                if (parseResult.isSuccessful() &&
                                parseResult.getResult().get().getStorage().isPresent() &&
                                shouldAddCU(parseResult.getResult().get().getStorage().get().getDirectory())) {
                    compilationUnits.add(parseResult.getResult().get());
                }
            });
        });

        return compilationUnits;
    }

    /**
     * Checks whether the {@link CompilationUnit} should be added to the list of {@link CompilationUnit}
     * @param path directory where file is located.
     * @return true if all checks have passed.
     */
    private static boolean shouldAddCU(Path path) {
        var directory = path.normalize();
        var dirNameList = Arrays.asList(directory.toString().split(getFileSeparatorForSplit()));
        return dirNameList.stream()
                .map(name -> !name.startsWith("."))
                .reduce(true, (x, y) -> x && y);
    }

    /**
     * @return File separator for splitting.
     */
    private static String getFileSeparatorForSplit() {
        if (File.separator.equals("\\")) return "\\\\";
        return File.separator;
    }


}
