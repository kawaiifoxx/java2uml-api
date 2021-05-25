package org.java2uml.java2umlapi.restControllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.java2uml.java2umlapi.fileStorage.entity.ProjectInfo;
import org.java2uml.java2umlapi.fileStorage.repository.ProjectInfoRepository;
import org.java2uml.java2umlapi.fileStorage.service.FileStorageService;
import org.java2uml.java2umlapi.fileStorage.service.UnzippedFileStorageService;
import org.java2uml.java2umlapi.modelAssemblers.ProjectInfoAssembler;
import org.java2uml.java2umlapi.parsedComponent.service.SourceComponentService;
import org.java2uml.java2umlapi.restControllers.error.ErrorResponse;
import org.java2uml.java2umlapi.restControllers.exceptions.BadRequest;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Objects;

/**
 * <p>
 * This file controller is main access point for interaction with the api.
 * all the requests to "/api/files" endpoint maps to this controller.
 * </p>
 *
 * @author kawaiifox
 */
@Tag(name = "File Upload", description = "Upload java projects for parsing")
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
     * @throws HttpMediaTypeNotSupportedException if file format is not "application/zip".
     */
    @Operation(summary = "upload source file, to explore, generate UML diagrams and much more.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Upload Successful"),
            @ApiResponse(responseCode = "500", description = "Something went wrong on our side. We will fix it promise!",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "415", description = "Please upload a zip file.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public EntityModel<ProjectInfo> upload(@RequestPart @RequestParam("file") MultipartFile file)
            throws HttpMediaTypeNotSupportedException {
        if (!Objects.requireNonNull(file.getContentType()).contains("zip")) {
            throw new HttpMediaTypeNotSupportedException("please upload file with zip format.");
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
