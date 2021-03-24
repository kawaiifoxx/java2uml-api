package org.java2uml.java2umlapi.fileStorage;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>
 * Stores Configuration properties file storage service and Unzipped file storage service.
 * </p>
 *
 * @author kawaiifox
 */
@ConfigurationProperties(prefix = "file")
public class FileStorageProperties {
    private String uploadDir;
    private String unzipDir;
    private String umlDir;

    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }

    public String getUmlDir() {
        return umlDir;
    }

    public void setUmlDir(String umlDir) {
        this.umlDir = umlDir;
    }

    public String getUnzipDir() {
        return unzipDir;
    }

    public void setUnzipDir(String unzipDir) {
        this.unzipDir = unzipDir;
    }
}
