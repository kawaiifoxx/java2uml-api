package com.shreyansh.ex1;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;

import java.io.File;
import java.util.regex.Pattern;

public class ModifyingVisitorComplete {
    private static final String FILE_PATH = "src/main/java/org/javaparser/examples/ReversePolishNotation.java";

    public static void main(String[] args) throws Exception {
        CompilationUnit cu = StaticJavaParser.parse(new File(FILE_PATH));
        ModifierVisitor<?> modifierVisitor = new IntegerLiteralModifier();
        modifierVisitor.visit(cu, null);

        System.out.println(cu);
    }

    private static final Pattern LOOK_AHEAD_THREE = Pattern.compile("(\\d)(?=(\\d{3})+$)");

    private static class IntegerLiteralModifier extends ModifierVisitor<Void> {
        @Override
        public Visitable visit(FieldDeclaration n, Void arg) {
            super.visit(n, arg);

            n.getVariables()
                    .forEach(
                            v -> v.getInitializer()
                                    .ifPresent(i -> {
                                        if (i instanceof IntegerLiteralExpr) {
                                            v.setInitializer(formatWithUnderscores(((IntegerLiteralExpr) i).getValue()));
                                        }
                                    }));

            return n;
        }
    }

    static String formatWithUnderscores(String value) {
        String withoutUnderscores = value.replaceAll("_", "");
        return LOOK_AHEAD_THREE.matcher(withoutUnderscores).replaceAll("$1_");
    }
}
