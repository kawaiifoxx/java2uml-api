package org.java2uml.java2umlapi.restControllers;

import org.java2uml.java2umlapi.fileStorage.entity.ProjectInfo;
import org.java2uml.java2umlapi.fileStorage.repository.ProjectInfoRepository;
import org.java2uml.java2umlapi.fileStorage.service.FileStorageService;
import org.java2uml.java2umlapi.fileStorage.service.UnzippedFileStorageService;
import org.java2uml.java2umlapi.modelAssemblers.ProjectInfoAssembler;
import org.java2uml.java2umlapi.parsedComponent.service.SourceComponentService;
import org.java2uml.java2umlapi.restControllers.exceptions.BadRequest;
import org.java2uml.java2umlapi.restControllers.exceptions.UnrecognizedFileFormatException;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * <p>
 * This file controller is main access point for interaction with the api.
 * all the requests to "/api/files" endpoint maps to this controller.
 * </p>
 *
 * @author kawaiifox
 */
@RestController
@RequestMapping("/api/files")
public class FileController {
    private final FileStorageService fileStorageService;
    private final ProjectInfoAssembler assembler;
    private final ProjectInfoRepository projectInfoRepository;
    private final UnzippedFileStorageService unzippedFileStorageService;
    private final SourceComponentService sourceComponentService;

    public FileController(FileStorageService fileStorageService,
                          ProjectInfoAssembler assembler,
                          ProjectInfoRepository projectInfoRepository,
                          UnzippedFileStorageService unzippedFileStorageService,
                          SourceComponentService sourceComponentService) {
        this.fileStorageService = fileStorageService;
        this.assembler = assembler;
        this.projectInfoRepository = projectInfoRepository;
        this.unzippedFileStorageService = unzippedFileStorageService;
        this.sourceComponentService = sourceComponentService;
    }

    /**
     * Provides post mapping for "/api/files" endpoint
     *
     * @param file Multipart file should be a zip file.
     * @return ProjectInfo containing meta data and useful links.
     * @throws UnrecognizedFileFormatException if file format is not "application/zip".
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public EntityModel<ProjectInfo> upload(@RequestParam("file") MultipartFile file) {
        if (file.getContentType() == null || !file.getContentType().contains("application/zip")) {
            throw new UnrecognizedFileFormatException(
                    "please upload file with zip format."
            );
        }

        String fileName = fileStorageService.store(file);
        File unzippedFile = unzippedFileStorageService.unzipAndStore(fileName);
        fileStorageService.delete(fileName);

        ProjectInfo projectInfo;
        try {
            projectInfo = projectInfoRepository.save(
                    new ProjectInfo(
                            unzippedFile.getName(),
                            fileName,
                            file.getSize(),
                            file.getContentType(),
                            sourceComponentService.save(unzippedFile.toPath())
                    )
            );
        } catch (BadRequest e) {
            unzippedFileStorageService.delete(unzippedFile.getName());
            throw e;
        }

        return assembler.toModel(projectInfo);
    }
}
