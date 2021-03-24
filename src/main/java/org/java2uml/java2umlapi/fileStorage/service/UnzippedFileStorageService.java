package org.java2uml.java2umlapi.fileStorage.service;

import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import org.apache.commons.io.FileDeleteStrategy;
import org.java2uml.java2umlapi.fileStorage.FileStorageProperties;
import org.java2uml.java2umlapi.fileStorage.exceptions.MyFileNotFoundException;
import org.java2uml.java2umlapi.fileStorage.exceptions.UnableToUnzipFileException;
import org.java2uml.java2umlapi.util.unzipper.Unzipper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/**
 * <p>
 * This is file storage service for unzipped file, it used for managing unzipped files.<br>
 * It reads properties, unzippedFile Location and uploadFile Location properties from
 * FileStorageProperties class.
 * </p>
 *
 * @author kawaiifox
 */
@Service
public class UnzippedFileStorageService {
    private final Path unzippedFileLocation;
    private final Path uploadFileLocation;
    private final Logger logger = LoggerFactory.getLogger(UnzippedFileStorageService.class);

    public UnzippedFileStorageService(FileStorageProperties fileStorageProperties) {
        this.unzippedFileLocation = Path.of(
                fileStorageProperties
                        .getUnzipDir()
        )
                .toAbsolutePath()
                .normalize();

        this.uploadFileLocation = Path.of(
                fileStorageProperties
                        .getUploadDir()
        )
                .toAbsolutePath()
                .normalize();
    }

    /**
     * Unzips a  given file and assigns a unique folder to it.
     * Files are stored in unzipped File location, this location can be changed from
     * application.properties file.
     *
     * @param filename name of the uploaded file present in upload file location.
     * @return Unzipped file
     */
    public File unzipAndStore(String filename) {
        File file;

        Path sourcePath = this.uploadFileLocation.resolve(filename).normalize();

        if (!Files.exists(sourcePath)) {
            throw new MyFileNotFoundException("File not found " + filename);
        }

        try {
            file = Unzipper.unzipDir(sourcePath, unzippedFileLocation.resolve(UUID.randomUUID().toString()));
        } catch (IOException exception) {
            throw new UnableToUnzipFileException("Unable to unzip, given file, please upload again.", exception);
        }

        return file;
    }

    /**
     * Finds the given file in unzipped file location.
     *
     * @param filename unzipped filename.
     * @return File
     * @throws MyFileNotFoundException if file is not found.
     */
    public File find(String filename) {
        File file = this.unzippedFileLocation.resolve(filename).toFile();

        if (!file.exists()) {
            throw new MyFileNotFoundException("the file you are looking for is not present.");
        }

        return file;
    }

    /**
     * <p>
     * Deletes file with provided filename in the unzipped file location given that file exists in the first place.
     * </p>
     * Underlying Implementation uses FORCE FileDeleteStrategy to delete the file.<br>
     * All the jar files are closed first using JarTypeSolver.ResourceRegistry.getRegistry().cleanUp()
     *
     * @param filename name of the file to be deleted in unzipped file location
     * @see FileDeleteStrategy
     * @see JarTypeSolver.ResourceRegistry
     */
    public void delete(String filename) {
        try {
            JarTypeSolver.ResourceRegistry.getRegistry().cleanUp();
            FileDeleteStrategy.FORCE.delete(this.find(filename));
        } catch (IOException exception) {
            logger.error("Unable to delete unzipped file!.", exception);
        }
    }
}
