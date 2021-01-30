package org.java2uml.java2umlapi.util;


import java.io.File;

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
         * @param level - current directory depth.
         * @param path - path of the current file.
         * @param file - file being handled.
         */
        void handle(int level, String path, File file);
    }

    /**
     * implement this interface and provide definition for interested method.
     */
    public interface Filter {
        /**
         * Filters interested files for performing some operation on them.
         * @param level - current directory depth.
         * @param path - path of the current file.
         * @param file - file being handled.
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
     * @param root - Source Directory.
     */
    public void explore(File root) {
        explore(0, "", root);
    }

    private void explore(int level, String path, File file) {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                explore(level + 1, path + "/" + child.getName(), child);
            }
        } else {
            if (filter.interested(level, path, file)) {
                fileHandler.handle(level, path, file);
            }
        }
    }

}
