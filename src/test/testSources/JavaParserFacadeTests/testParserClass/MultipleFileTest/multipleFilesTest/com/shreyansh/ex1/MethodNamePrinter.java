package com.shreyansh.ex1;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class MethodNamePrinter extends VoidVisitorAdapter<Void> {
    @Override
    public void visit(MethodDeclaration n, Void arg) {
        super.visit(n, arg);

        System.out.println("Method Declaration name: " + n.getName());
    }
}
