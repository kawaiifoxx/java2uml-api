package com.shreyansh.ex1;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class GetComments {
    private static class CommentReportEntry {
        private final String type;
        private final String text;
        private final int lineNumber;
        private final boolean isOrphan;

        CommentReportEntry(String type, String text, int lineNumber, boolean isOrphan) {
            this.type = type;
            this.text = text;
            this.lineNumber = lineNumber;
            this.isOrphan = isOrphan;
        }


        @Override
        public String toString() {
            return lineNumber + "|" + type + "|" + isOrphan + "|" +
                    text.replaceAll("\\n", "").trim();
        }
    }

    private static final String FILE_PATH = "src/main/java/org/javaparser/examples/ReversePolishNotation.java";

    public static void main(String[] args) throws Exception {
        CompilationUnit cu = StaticJavaParser.parse(new File(FILE_PATH));
        List<CommentReportEntry> comments = cu.getAllComments()
                .stream()
                .map(p -> new CommentReportEntry(p.getClass().getSimpleName(),
                        p.getContent(),
                        p.getRange().get().begin.line,
                        !p.getCommentedNode().isPresent()))
                .collect(Collectors.toList());

        comments.forEach(System.out::println);

    }
}
