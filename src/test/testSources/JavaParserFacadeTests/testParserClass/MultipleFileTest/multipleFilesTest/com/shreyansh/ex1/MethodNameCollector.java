package com.shreyansh.ex1;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.List;

public class MethodNameCollector extends VoidVisitorAdapter<List<String>> {

    @Override
    public void visit(ClassOrInterfaceDeclaration n, List<String> arg) {
        super.visit(n, arg);

        arg.add(n.getNameAsString());
    }
}
