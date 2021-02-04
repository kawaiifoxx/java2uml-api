package org.java2uml.java2umlapi.parser;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * Collects all the ClassOrInterfaceDeclarations, in a list.
 * </p>
 *
 * @author kawaiifoxx
 * @see com.github.javaparser.ast.visitor.VoidVisitor
 */
@Component
public final class ClassOrInterfaceCollector extends VoidVisitorAdapter<List<ClassOrInterfaceDeclaration>> {
    /**
     * <p>
     * visits the whole ast in search of ClassOrInterfaceDeclarations.
     * </p>
     *
     * @param n   - ClassOrInterfaceDeclaration node in current ast.
     * @param arg - state to be stored, in this case a Map<String, ClassOrInterfaceDeclaration>
     */
    @Override
    public void visit(ClassOrInterfaceDeclaration n, List<ClassOrInterfaceDeclaration> arg) {
        super.visit(n, arg);
        arg.add(n);
    }
}
