package org.java2uml.java2umlapi.restControllers.LWControllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.java2uml.java2umlapi.fileStorage.entity.ProjectInfo;
import org.java2uml.java2umlapi.fileStorage.repository.ProjectInfoRepository;
import org.java2uml.java2umlapi.lightWeight.Source;
import org.java2uml.java2umlapi.lightWeight.repository.MethodRepository;
import org.java2uml.java2umlapi.lightWeight.repository.SourceRepository;
import org.java2uml.java2umlapi.lightWeight.service.MethodSignatureToMethodIdMapService;
import org.java2uml.java2umlapi.modelAssemblers.SourceAssembler;
import org.java2uml.java2umlapi.parsedComponent.SourceComponent;
import org.java2uml.java2umlapi.parsedComponent.service.SourceComponentService;
import org.java2uml.java2umlapi.restControllers.exceptions.CannotGenerateSourceException;
import org.java2uml.java2umlapi.restControllers.exceptions.LightWeightNotFoundException;
import org.java2uml.java2umlapi.restControllers.exceptions.ParsedComponentNotFoundException;
import org.java2uml.java2umlapi.restControllers.exceptions.ProjectInfoNotFoundException;
import org.java2uml.java2umlapi.restControllers.response.ErrorResponse;
import org.java2uml.java2umlapi.visitors.lightWeightExtractor.specialized.LightWeightExtractorWithMethodSignatureCache;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.java2uml.java2umlapi.restControllers.SwaggerDescription.*;

/**
 * <p>
 * Rest Controller for {@link Source} entities.
 * </p>
 *
 * @author kawaiifox.
 */
@Tag(name = "Source", description = "Source represents source code of your project.")
@RestController
@RequestMapping("/api/source")
public class SourceController {
    private final SourceRepository sourceRepository;
    private final SourceAssembler assembler;
    private final ProjectInfoRepository projectInfoRepository;
    private final MethodRepository methodRepository;
    private final SourceComponentService sourceComponentService;
    private final MethodSignatureToMethodIdMapService methodIdMapService;

    public SourceController(
            SourceRepository sourceRepository,
            SourceAssembler assembler,
            ProjectInfoRepository projectInfoRepository,
            MethodRepository methodRepository,
            SourceComponentService sourceComponentService,
            MethodSignatureToMethodIdMapService methodIdMapService
    ) {
        this.sourceRepository = sourceRepository;
        this.assembler = assembler;
        this.projectInfoRepository = projectInfoRepository;
        this.methodRepository = methodRepository;
        this.sourceComponentService = sourceComponentService;
        this.methodIdMapService = methodIdMapService;
    }

    /**
     * Retrieves {@link Source} with provided id.
     *
     * @param sourceId id of {@link Source} to be retrieved.
     * @return {@link EntityModel} of {@link Source} with useful links.
     * @throws LightWeightNotFoundException if {@link Source} is not found.
     */
    @Operation(summary = "Get Source", description = "get the source by source id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = OK_200_RESPONSE),
            @ApiResponse(responseCode = "404", description = NOT_FOUND_404,
                    content = @Content(mediaType = ERR_RESPONSE_MEDIA_TYPE,
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{sourceId}")
    public EntityModel<Source> one(@Parameter(description = SOURCE_ID_DESC) @PathVariable("sourceId") Long sourceId) {
        return assembler.toModel(
                sourceRepository.findById(sourceId)
                        .orElseThrow(
                                () -> new LightWeightNotFoundException(
                                        "Unable to fetch source with id " + sourceId
                                )
                        )
        );
    }

    /**
     * Retrieves {@link Source} by {@link ProjectInfo} id if present, else this method tries to generate {@link Source}.
     *
     * @param projectInfoId id of {@link ProjectInfo}
     * @return {@link EntityModel} of {@link Source} with useful links.
     * @throws ProjectInfoNotFoundException     if {@link ProjectInfo} is not found.
     * @throws CannotGenerateSourceException    if {@link Source} cannot be generated.
     * @throws ParsedComponentNotFoundException if {@link SourceComponent} is not found.
     */
    @Operation(summary = "Get Source", description = "get the source by ProjectInfo id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = OK_200_RESPONSE),
            @ApiResponse(responseCode = "404", description = NOT_FOUND_404,
                    content = @Content(mediaType = ERR_RESPONSE_MEDIA_TYPE,
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/by-project-info/{projectInfoId}")
    public EntityModel<Source> findByProjectId(
            @Parameter(description = PROJECT_ID_DESC) @PathVariable("projectInfoId") Long projectInfoId) {
        var projectInfo = projectInfoRepository.findById(projectInfoId)
                .orElseThrow(() -> new ProjectInfoNotFoundException("The information about file you were looking " +
                        "for is not present. please consider, uploading the given file again."));
        return assembler.toModel(getSource(projectInfo));
    }

    /**
     * if {@link Source} has not been generated then this method generates {@link Source} and returns it.
     * This method also updates {@link ProjectInfo} with the generated {@link Source}, if originally
     * {@link Source} was not present.
     *
     * @param projectInfo for which {@link Source} is needed.
     * @return {@link Source}
     */
    private Source getSource(ProjectInfo projectInfo) {
        //Extract source if not present.
        if (projectInfo.getSource() == null) {
            extractSource(projectInfo);
        }
        return projectInfo.getSource();
    }

    /**
     * Extracts {@link Source} from {@link SourceComponent} and if {@link Source} is successfully extracted.<br>
     * Sets {@link Source} in {@link ProjectInfo}.<br>
     * Saves {@link ProjectInfo}.<br>
     * Saves methodNameToMethodIdMap in {@link MethodSignatureToMethodIdMapService}.
     *
     * @param projectInfo in which {@link Source} will be set.
     * @throws CannotGenerateSourceException if source cannot be generated for some reason.
     */
    private void extractSource(ProjectInfo projectInfo) {
        SourceComponent sourceComponent = getSourceComponent(projectInfo);
        var extractor = new LightWeightExtractorWithMethodSignatureCache(methodRepository);
        projectInfo.setSource(
                sourceRepository.save(
                        sourceComponent.accept(extractor)
                                .asSource()
                                .orElseThrow(
                                        () -> new CannotGenerateSourceException(
                                                "Unable to fetch source," + " Please try again later."
                                        )
                                )
                )
        );
        var source = projectInfo.getSource();
        source.setProjectInfo(projectInfo);
        projectInfoRepository.save(projectInfo);
        methodIdMapService.save(source.getId(), extractor.getSignatureToIdMap());
    }

    /**
     * @param projectInfo for which {@link SourceComponent} needs to be fetched.
     * @return {@link SourceComponent}
     * @throws ParsedComponentNotFoundException if {@link SourceComponent} is not found.
     */
    private SourceComponent getSourceComponent(ProjectInfo projectInfo) {
        return sourceComponentService.get(projectInfo.getId())
                .orElseThrow(() -> new ParsedComponentNotFoundException("Unable to fetch source component," +
                        " uploading files again should fix this."));
    }
}
