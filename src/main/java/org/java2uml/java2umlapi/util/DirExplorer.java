package org.java2uml.java2umlapi.util;


import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import java.io.File;
import java.util.Map;

/**
 * Explores directory recursively, looks for interested files to
 * perform some operation on them.
 */
public class DirExplorer {
    /**
     * implement this interface and provide definition for handle method.
     */
    public interface FileHandler {
        /**
         * performs some operation on interested file.
         *
         * @param level current directory depth.
         * @param path  path of the current file.
         * @param file  file being handled.
         * @param state mapping from node name to node object.
         */
        void handle(int level, String path, File file, Map<String, ClassOrInterfaceDeclaration> state);
    }

    /**
     * implement this interface and provide definition for interested method.
     */
    public interface Filter {
        /**
         * Filters interested files for performing some operation on them.
         *
         * @param level current directory depth.
         * @param path  path of the current file.
         * @param file  file being handled.
         * @return - returns true if some operation should be performed on the file.
         */
        boolean interested(int level, String path, File file);
    }

    private FileHandler fileHandler;
    private Filter filter;

    public DirExplorer(Filter filter, FileHandler fileHandler) {
        this.filter = filter;
        this.fileHandler = fileHandler;
    }

    /**
     * Explores the directory tree in search of interesting files to perform some operation on them.
     *
     * @param root  Source Directory.
     * @param state Mapping between nodeName:String -> node:Node
     */
    public void explore(File root, Map<String, ClassOrInterfaceDeclaration> state) {
        explore(0, "", root, state);
    }

    private void explore(int level, String path, File file, Map<String, ClassOrInterfaceDeclaration> state) {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                explore(level + 1, path + "/" + child.getName(), child, state);
            }
        } else {
            if (filter.interested(level, path, file)) {
                fileHandler.handle(level, path, file, state);
            }
        }
    }

}
