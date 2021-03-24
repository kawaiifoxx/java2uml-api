package org.java2uml.java2umlapi.fileStorage.service;

import org.apache.commons.io.FileDeleteStrategy;
import org.java2uml.java2umlapi.fileStorage.FileStorageProperties;
import org.java2uml.java2umlapi.fileStorage.exceptions.FileStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

/**
 * <p>
 * A Service for storing MultiPartFiles in file system.<br>
 * This supports two operations storing and deleting files.
 * </p>
 *
 * @author kawaiifox
 */
@Service
public class FileStorageService {
    private final Path fileStorageLocation;
    private final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    public FileStorageService(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath()
                .normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception e) {
            throw new FileStorageException(
                    "Could not create the directory where the uploaded files will be stored.", e
            );
        }
    }

    /**
     * Stores the provided multipart file in the file system.
     *
     * @param file to be stored.
     * @return name of the stored file.
     */
    public String store(MultipartFile file) {
        // Normalize file name
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    /**
     * Deletes file with given file name if present else if file does not exist logs that file is not present.
     *
     * @param fileName name of the file to be deleted.
     */
    public void delete(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            if (Files.exists(filePath)) {
                FileDeleteStrategy.NORMAL.delete(filePath.toFile());
                return;
            }
            logger.info("File with name {} is not present.", fileName);
        } catch (IOException exception) {
            logger.error("Unable to delete file.", exception);
        }
    }
}
