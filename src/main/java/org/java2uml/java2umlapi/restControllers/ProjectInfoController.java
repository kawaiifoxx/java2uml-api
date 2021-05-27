package org.java2uml.java2umlapi.restControllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.java2uml.java2umlapi.fileStorage.entity.ProjectInfo;
import org.java2uml.java2umlapi.fileStorage.repository.ProjectInfoRepository;
import org.java2uml.java2umlapi.fileStorage.service.UnzippedFileStorageService;
import org.java2uml.java2umlapi.lightWeight.service.MethodSignatureToMethodIdMapService;
import org.java2uml.java2umlapi.modelAssemblers.ProjectInfoAssembler;
import org.java2uml.java2umlapi.parsedComponent.service.SourceComponentService;
import org.java2uml.java2umlapi.restControllers.error.ErrorResponse;
import org.java2uml.java2umlapi.restControllers.exceptions.ProjectInfoNotFoundException;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static org.java2uml.java2umlapi.restControllers.SwaggerDescription.*;

/**
 * <p>
 * The {@link ProjectInfoController} is a spring mvc rest controller for {@link ProjectInfo} entity.<br>
 * All  the requests at "/api/project-info" endpoint will be routed to this controller.
 * </p>
 *
 * @author kawaiifox
 */
@Tag(name = "Manage Project", description = "Query about uploaded project or delete them.")
@RestController
@RequestMapping("/api/project-info")
public class ProjectInfoController {
    private final ProjectInfoRepository projectInfoRepository;
    private final ProjectInfoAssembler assembler;
    private final UnzippedFileStorageService unzippedFileStorageService;
    private final SourceComponentService sourceComponentService;
    private final MethodSignatureToMethodIdMapService methodSignatureToMethodIdMapService;

    public ProjectInfoController(
            ProjectInfoRepository projectInfoRepository,
            ProjectInfoAssembler assembler,
            UnzippedFileStorageService unzippedFileStorageService,
            SourceComponentService sourceComponentService,
            MethodSignatureToMethodIdMapService methodSignatureToMethodIdMapService
    ) {
        this.projectInfoRepository = projectInfoRepository;
        this.assembler = assembler;
        this.unzippedFileStorageService = unzippedFileStorageService;
        this.sourceComponentService = sourceComponentService;
        this.methodSignatureToMethodIdMapService = methodSignatureToMethodIdMapService;
    }

    /**
     * Defines a get mapping for "/api/project-info" endpoint, this method retrieves {@link ProjectInfo}
     * instances for provided id.
     *
     * @param projectId id of the {@link ProjectInfo} that you want to retrieve.
     * @return {@link EntityModel<ProjectInfo>} with some useful links.
     * @throws ProjectInfoNotFoundException if {@link ProjectInfo} has not been found.
     */
    @Operation(summary = "Query a project.", description = "Query an existing project by providing its id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = OK_200_RESPONSE),
            @ApiResponse(responseCode = "404", description = "Project Info, " + NOT_FOUND_404,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{projectId}")
    public EntityModel<ProjectInfo> one(
            @Parameter(description = PROJECT_ID_DESC) @PathVariable("projectId") Long projectId) {
        return assembler.toModel(projectInfoRepository
                .findById(projectId)
                .orElseThrow(
                        () -> new ProjectInfoNotFoundException("The information about file you were looking " +
                                "for is not present. please consider, uploading the given file again.")
                ));
    }

    /**
     * Defines a delete mapping for "/api/project-info" endpoint, this method deletes the {@link ProjectInfo} instance
     * as well as files related to the project, sourceComponent, methodSignatureToMethodIdMap.
     *
     * @param projectId id of the {@link ProjectInfo} you want to delete.
     * @return Http no content response.
     */
    @Operation(summary = "Delete a project.", description = "Delete an existing project by providing its id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = DELETE_SUCCESS_204,
                    content = @Content(schema = @Schema)
            ),
            @ApiResponse(responseCode = "404", description = "Project Info, " + NOT_FOUND_404,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @DeleteMapping("/{projectId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Object delete(@PathVariable Long projectId) {
        ProjectInfo projectInfo = projectInfoRepository.findById(projectId).orElseThrow(
                () -> new ProjectInfoNotFoundException(
                        "The information about file with id " + projectId
                                + " you were looking for is not present. please consider, uploading the file again."
                )
        );

        sourceComponentService.delete(projectInfo.getSourceComponentId());
        unzippedFileStorageService.delete(projectInfo.getUnzippedFileName());
        methodSignatureToMethodIdMapService.delete(projectId);
        projectInfoRepository.delete(projectInfo);
        return null;
    }
}
