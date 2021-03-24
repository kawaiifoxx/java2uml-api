package org.java2uml.java2umlapi.restControllers;

import org.java2uml.java2umlapi.fileStorage.entity.ProjectInfo;
import org.java2uml.java2umlapi.fileStorage.repository.ProjectInfoRepository;
import org.java2uml.java2umlapi.fileStorage.service.UnzippedFileStorageService;
import org.java2uml.java2umlapi.lightWeight.service.MethodSignatureToMethodIdMapService;
import org.java2uml.java2umlapi.modelAssemblers.ProjectInfoAssembler;
import org.java2uml.java2umlapi.parsedComponent.service.SourceComponentService;
import org.java2uml.java2umlapi.restControllers.exceptions.ProjectInfoNotFoundException;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * The ProjectInfo controller is a spring mvc rest controller for project info entity.<br>
 * All  the requests at "/api/project-info" endpoint will be routed to this controller.
 * </p>
 *
 * @author kawaiifox
 */
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
     * Defines a get mapping for "/api/project-info" endpoint, this method retrieves project info
     * instances for provided id.
     * @param projectId id of the projectInfo that you want to retrieve.
     * @return projectInfo with some useful links.
     */
    @GetMapping("/{projectId}")
    public EntityModel<ProjectInfo> one(@PathVariable("projectId") Long projectId) {
        return assembler.toModel(projectInfoRepository
                .findById(projectId)
                .orElseThrow(
                        () -> new ProjectInfoNotFoundException("The information about file you were looking " +
                                "for is not present. please consider, uploading the given file again.")
                ));
    }

    /**
     * Defines a delete mapping for "/api/project-info" endpoint, this method deletes the project info instance
     * as well as files related to the project, sourceComponent, methodSignatureToMethodIdMap.
     * @param projectId id of the project you want to delete.
     * @return Http no content response.
     */
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
        unzippedFileStorageService.delete(projectInfo.getProjectName());
        methodSignatureToMethodIdMapService.delete(projectId);
        projectInfoRepository.delete(projectInfo);
        return null;
    }
}
