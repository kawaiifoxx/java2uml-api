package org.java2uml.java2umlapi.restControllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.java2uml.java2umlapi.executor.ExecutorWrapper;
import org.java2uml.java2umlapi.fileStorage.entity.ProjectInfo;
import org.java2uml.java2umlapi.fileStorage.repository.ProjectInfoRepository;
import org.java2uml.java2umlapi.fileStorage.service.FileStorageService;
import org.java2uml.java2umlapi.fileStorage.service.UnzippedFileStorageService;
import org.java2uml.java2umlapi.modelAssemblers.ProjectInfoAssembler;
import org.java2uml.java2umlapi.parsedComponent.service.SourceComponentService;
import org.java2uml.java2umlapi.restControllers.exceptions.BadRequest;
import org.java2uml.java2umlapi.restControllers.response.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.java2uml.java2umlapi.restControllers.SwaggerDescription.ERR_RESPONSE_MEDIA_TYPE;
import static org.java2uml.java2umlapi.restControllers.SwaggerDescription.INTERNAL_SERVER_ERROR_DESC;

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
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    private final ProjectInfoAssembler assembler;
    private final ProjectInfoRepository projectInfoRepository;
    private final UnzippedFileStorageService unzippedFileStorageService;
    private final SourceComponentService sourceComponentService;
    private final ExecutorWrapper executor;
    private static final Long TIME_OUT = 4L;
    private final Logger logger = LoggerFactory.getLogger(FileController.class);

    public FileController(FileStorageService fileStorageService,
                          ProjectInfoAssembler assembler,
                          ProjectInfoRepository projectInfoRepository,
                          UnzippedFileStorageService unzippedFileStorageService,
                          SourceComponentService sourceComponentService,
                          ExecutorWrapper executor) {
        this.fileStorageService = fileStorageService;
        this.assembler = assembler;
        this.projectInfoRepository = projectInfoRepository;
        this.unzippedFileStorageService = unzippedFileStorageService;
        this.sourceComponentService = sourceComponentService;
        this.executor = executor;
    }

    /**
     * Provides post mapping for "/api/files" endpoint
     *
     * @param file Multipart file should be a zip file.
     * @return ProjectInfo containing meta data and useful links.
     * @throws HttpMediaTypeNotSupportedException if file format is not "application/zip".
     * @throws InterruptedException               if {@link ExecutorWrapper} is interrupted.
     */
    @Operation(summary = "Upload Source",
            description = "upload source file, to explore, generate UML diagrams and much more.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Upload Successful"),
            @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR_DESC,
                    content = @Content(mediaType = ERR_RESPONSE_MEDIA_TYPE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "415", description = "Please upload a zip file.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public EntityModel<ProjectInfo> upload(@RequestPart @RequestParam("file") MultipartFile file)
            throws HttpMediaTypeNotSupportedException, InterruptedException {
        if (!Objects.requireNonNull(file.getContentType()).contains("zip")) {
            throw new HttpMediaTypeNotSupportedException("please upload file with zip format.");
        }

        String fileName = fileStorageService.store(file);
        ProjectInfo projectInfo = projectInfoRepository
                .save(new ProjectInfo(fileName, file.getSize(), file.getContentType()));

        return getProjectInfoResponse(fileName, projectInfo);
    }

    /**
     * Generate response for project info.
     *
     * @param fileName    name of the file to be parsed.
     * @param projectInfo project info for which response needs to be generated.
     * @return Generated response.
     * @throws InterruptedException if any of {@link ExecutorWrapper}' s thread is interrupted b/w activity.
     */
    private EntityModel<ProjectInfo> getProjectInfoResponse(String fileName, ProjectInfo projectInfo) throws InterruptedException {
        var future = executor.submit(() -> {
            var unzippedFile = unzippedFileStorageService.unzipAndStore(projectInfo.getId(), fileName);
            fileStorageService.delete(fileName);
            try {
                sourceComponentService.save(projectInfo.getId(), unzippedFile.toPath());
                sourceComponentService.get(projectInfo.getId()).ifPresent(sourceComponent -> {
                    if (!sourceComponent.isExternalDependenciesIncluded())
                        projectInfo.addMessage(
                                "Please add all the project dependencies in zip file for better parsing results.");
                });
            } catch (BadRequest e) {
                projectInfo.setBadRequest(true);
                projectInfoRepository.save(projectInfo);
                throw e;
            }

            projectInfo.setParsed(true);
            projectInfoRepository.save(projectInfo);
        });

        try {
            future.get(TIME_OUT, TIME_UNIT);
        } catch (InterruptedException e) {
            logger.warn("ExecutorWrapper's thread was interrupted while parsing project.");
            throw e;
        } catch (ExecutionException e) {
            if (e.getCause() instanceof BadRequest) {
                unzippedFileStorageService.delete(projectInfo.getId());
                throw (BadRequest) e.getCause();
            }
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "an unknown error occurred.");
        } catch (TimeoutException ignored) {
            logger.info("Time Out occurred, proceeding with task at hand.");
        }

        return assembler.toModel(projectInfo);
    }
}
