package com.shreyansh.ex1;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.printer.DotPrinter;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class SimpleVisitor {

    private static final String FILE_PATH = "src/main/java/org/javaparser/examples/ReversePolishNotation.java";

    public static void main(String[] args) throws Exception {
        CompilationUnit cu = StaticJavaParser.parse(new File(FILE_PATH));

//        DotPrinter printer = new DotPrinter(true);
//
//        try(FileWriter fileWriter = new FileWriter("ast.dot")) {
//            PrintWriter printWriter = new PrintWriter(fileWriter);
//
//            printWriter.print(printer.output(cu));
//        } catch (Exception exception) {
//            exception.printStackTrace();
//        }

        System.out.println(cu);

        VoidVisitor<?> visitor = new MethodNamePrinter();
        visitor.visit(cu, null);
        VoidVisitor<List<String>> visitor1 = new MethodNameCollector();
        List<String> methodNames = new ArrayList<>();

        visitor1.visit(cu, methodNames);


        methodNames.forEach(System.out::println);
    }
}
