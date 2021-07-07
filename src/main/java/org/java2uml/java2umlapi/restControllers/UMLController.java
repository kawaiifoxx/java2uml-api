package org.java2uml.java2umlapi.restControllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import org.java2uml.java2umlapi.executor.ExecutorWrapper;
import org.java2uml.java2umlapi.fileStorage.entity.ProjectInfo;
import org.java2uml.java2umlapi.fileStorage.repository.ProjectInfoRepository;
import org.java2uml.java2umlapi.fileStorage.service.ClassDiagramSVGService;
import org.java2uml.java2umlapi.fileStorage.service.UMLCodeCacheService;
import org.java2uml.java2umlapi.lightWeight.UMLBody;
import org.java2uml.java2umlapi.modelAssemblers.UMLBodyAssembler;
import org.java2uml.java2umlapi.parsedComponent.SourceComponent;
import org.java2uml.java2umlapi.parsedComponent.service.SourceComponentService;
import org.java2uml.java2umlapi.restControllers.exceptions.CannotGenerateSVGException;
import org.java2uml.java2umlapi.restControllers.exceptions.ParsedComponentNotFoundException;
import org.java2uml.java2umlapi.restControllers.exceptions.ProjectInfoNotFoundException;
import org.java2uml.java2umlapi.restControllers.response.ErrorResponse;
import org.java2uml.java2umlapi.restControllers.response.TryAgainResponse;
import org.java2uml.java2umlapi.restControllers.services.SSEEmitterCache;
import org.java2uml.java2umlapi.visitors.umlExtractor.UMLExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.java2uml.java2umlapi.restControllers.SwaggerDescription.*;
import static org.java2uml.java2umlapi.restControllers.services.SSEEmitterCache.SSEventType.UML_CODE_GENERATION;
import static org.java2uml.java2umlapi.restControllers.services.SSEEmitterCache.SSEventType.UML_SVG_GENERATION;

/**
 * <p>
 * This controller handles all the requests having path "/api/uml".<br>
 * This controller performs generation of uml code and uml svg.
 * </p>
 *
 * @author kawaiifox
 */
@Tag(name = "Generate Class Diagrams", description = "generate plant uml code and plant uml class diagrams," +
        " Note: pls upload the project files first then use any get requests.")
@RestController
@RequestMapping("/api/uml")
public class UMLController {
    enum Result {SUCCEEDED}

    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    private final UMLBodyAssembler umlBodyAssembler;
    private final ProjectInfoRepository projectInfoRepository;
    private final SourceComponentService sourceComponentService;
    private final ClassDiagramSVGService classDiagramSVGService;
    private final UMLCodeCacheService umlCodeCacheService;
    private final SSEEmitterCache emitterCache;
    private final ExecutorWrapper executor;
    private final Logger logger = LoggerFactory.getLogger(UMLController.class);
    private static final Long TIME_OUT = 2L;

    public UMLController(
            UMLBodyAssembler umlBodyAssembler,
            ProjectInfoRepository projectInfoRepository,
            SourceComponentService sourceComponentService,
            ClassDiagramSVGService classDiagramSVGService,
            UMLCodeCacheService umlCodeCacheService,
            SSEEmitterCache emitterCache,
            ExecutorWrapper executor
    ) {
        this.umlBodyAssembler = umlBodyAssembler;
        this.projectInfoRepository = projectInfoRepository;
        this.sourceComponentService = sourceComponentService;
        this.classDiagramSVGService = classDiagramSVGService;
        this.umlCodeCacheService = umlCodeCacheService;
        this.emitterCache = emitterCache;
        this.executor = executor;
    }


    /**
     * This method defines get mapping for "/api/uml/plant-uml-code/{projectInfoId}"<br>
     * uml code is generated and then {@link EntityModel} containing uml code and useful links is returned.
     *
     * @param projectInfoId id of {@link ProjectInfo}
     * @return {@link EntityModel} of {@link UMLBody}
     * @throws ProjectInfoNotFoundException     if {@link ProjectInfo} is not found.
     * @throws ParsedComponentNotFoundException if {@link SourceComponent} is not found.
     */
    @Operation(summary = "Generate Plant UML.",
            description = "generate plant uml code from uploaded java source code.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Generation Successful"),
            @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR_DESC,
                    content = @Content(mediaType = ERR_RESPONSE_MEDIA_TYPE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = SOURCE_CODE_NOT_FOUND_DESC,
                    content = @Content(mediaType = ERR_RESPONSE_MEDIA_TYPE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "202", description = SwaggerDescription.ACCEPTED_DESC,
                    content = @Content(mediaType = ERR_RESPONSE_MEDIA_TYPE,
                            schema = @Schema(implementation = TryAgainResponse.class)))
    })
    @GetMapping("/plant-uml-code/{projectInfoId}")
    public EntityModel<UMLBody> getPUMLCode(@Parameter(description = PROJECT_ID_DESC) @PathVariable Long projectInfoId) {
        if (umlCodeCacheService.contains(projectInfoId))
            return umlBodyAssembler.toModel(new UMLBody(umlCodeCacheService.get(projectInfoId), projectInfoId));

        var projectInfo = getProjectInfo(projectInfoId);
        SourceComponent sourceComponent = getSourceComponent(projectInfo);

        var pUMLCode = getFromFuture(executor.submit(() -> {
            var code = umlCodeCacheService.save(projectInfo.getId(), sourceComponent.accept(new UMLExtractor()));
            notifyAboutPUMLCodeGeneration(projectInfo.getId());
            return code;
        }));
        return umlBodyAssembler.toModel(new UMLBody(pUMLCode, projectInfo.getId()));
    }

    /**
     * This method defines get mapping for "/api/uml/svg/{projectInfoId}"<br>
     * Svg is generated from plant uml code and {@link ResponseEntity} containing this svg is returned
     * content type "image/svg+xml"
     *
     * @param projectInfoId id of the {@link ProjectInfo}
     * @return UML in form of SVG
     * @throws ParsedComponentNotFoundException if {@link SourceComponent} is not found.
     * @throws CannotGenerateSVGException       if Svg cannot be generated.
     */
    @Operation(summary = "Generate Class Diagram",
            description = "generate plant uml class diagram svg from uploaded java source code.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Generation Successful"),
            @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR_DESC,
                    content = @Content(mediaType = ERR_RESPONSE_MEDIA_TYPE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = SOURCE_CODE_NOT_FOUND_DESC,
                    content = @Content(mediaType = ERR_RESPONSE_MEDIA_TYPE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "202", description = SwaggerDescription.ACCEPTED_DESC,
                    content = @Content(mediaType = ERR_RESPONSE_MEDIA_TYPE,
                            schema = @Schema(implementation = TryAgainResponse.class)))
    })
    @GetMapping(value = "/svg/{projectInfoId}", produces = {"image/svg+xml", "application/json", "application/json+hal"})
    public ResponseEntity<String> getSvg(@Parameter(description = PROJECT_ID_DESC) @PathVariable Long projectInfoId) {
        ProjectInfo projectInfo = getProjectInfo(projectInfoId);
        SourceComponent sourceComponent = getSourceComponent(projectInfo);
        return generateResponse(projectInfo, sourceComponent);
    }

    /**
     * Generates an svg if possible within the time frame else
     * generates an intermediate response asking the client to wait.
     *
     * @param projectInfo     {@link ProjectInfo} for which response needs to generated.
     * @param sourceComponent {@link SourceComponent} from which svg will be generated.
     * @return Generated {@link ResponseEntity}
     */
    private ResponseEntity<String> generateResponse(ProjectInfo projectInfo, SourceComponent sourceComponent) {
        String svg = getFromFuture(executor.submit(() -> generateSVG(sourceComponent, projectInfo.getId())));

        return ResponseEntity.ok().contentType(MediaType.parseMediaType("image/svg+xml"))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment;filename=\"" + getFileName(projectInfo) + "\""
                ).body(svg);
    }

    /**
     * Get the generated response from the provided future Object.
     *
     * @param future from which response will be extracted.
     * @return a {@link String} extracted from future.
     * @throws ResponseStatusException if {@link ExecutionException} or {@link InterruptedException} or
     *                                 {@link TimeoutException} occurs while extracting from {@link Future}
     *                                 this exception is thrown.
     */
    private String getFromFuture(Future<String> future) {
        try {
            return future.get(TIME_OUT, TIME_UNIT);
        } catch (ExecutionException e) {
            logger.warn("Exception caused due to {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (InterruptedException e) {
            logger.warn("Executor thread interrupted in b/w.");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (TimeoutException e) {
            logger.info("Timed Out, moving on with task at hand.");
            throw new ResponseStatusException(HttpStatus.ACCEPTED,
                    "Your, request is being processed check back in a few seconds.");
        }
    }

    /**
     * Generates svg from given plant uml code.
     *
     * @param sourceComponent SourceComponent from which uml will be extracted.
     * @param projectInfoId   id of {@link ProjectInfo} to check if project info is present.
     * @return Generated svg
     * @throws CannotGenerateSVGException if svg cannot be generated.
     */
    private String generateSVG(SourceComponent sourceComponent, Long projectInfoId) {
        if (classDiagramSVGService.contains(projectInfoId)) {
            return classDiagramSVGService.get(projectInfoId);
        }

        final ByteArrayOutputStream os;
        try {
            var reader = new SourceStringReader(sourceComponent.accept(new UMLExtractor()));
            os = new ByteArrayOutputStream();
            //noinspection deprecation
            reader.generateImage(os, new FileFormatOption(FileFormat.SVG));
            os.close();
        } catch (NullPointerException | IOException exception) {
            throw new CannotGenerateSVGException(
                    "Server encountered some error while generating svg, please try again later.",
                    exception
            );
        }

        notifyAboutSVGGeneration(projectInfoId);
        return classDiagramSVGService.save(projectInfoId, os.toString());
    }

    /**
     * @param projectInfo Used to retrieve {@link ProjectInfo} id
     * @return {@link SourceComponent} associated with the provided {@link ProjectInfo} is returned.
     */
    private SourceComponent getSourceComponent(ProjectInfo projectInfo) {
        return sourceComponentService.get(projectInfo.getId()).
                orElseThrow(
                        () -> new ParsedComponentNotFoundException("Please, upload your file again.")
                );
    }

    /**
     * @param projectInfoId id for which  {@link ProjectInfo} will be retrieved
     * @return {@link ProjectInfo} associated with the provided id.
     */
    private ProjectInfo getProjectInfo(Long projectInfoId) {
        return projectInfoRepository.findById(projectInfoId)
                .orElseThrow(() -> new ProjectInfoNotFoundException("The information about file you were looking " +
                        "for is not present. please consider, uploading the given file again."));
    }

    /**
     * Checks if .zip is present in project name if it is present then this method replaces it with .svg.
     * otherwise appends .svg to the project name.
     *
     * @param projectInfo {@link ProjectInfo} for which svg is being generated.
     * @return file name for generated svg.
     */
    private String getFileName(ProjectInfo projectInfo) {
        var fileName = projectInfo.getProjectName();
        if (fileName.contains(".zip")) {
            fileName = fileName.replace(".zip", ".svg");
        } else {
            fileName = fileName + ".svg";
        }
        return fileName;
    }


    /**
     * @param id {@link ProjectInfo} id
     */
    private void notifyAboutSVGGeneration(Long id) {
        if (!emitterCache.contains(id, UML_SVG_GENERATION)) return;

        SseEmitter emitter = emitterCache.get(id, UML_SVG_GENERATION);
        sendEvent(
                emitter,
                SseEmitter.event()
                        .name("UmlSvgGeneration")
                        .data(Result.SUCCEEDED)
        );
    }


    /**
     * @param id {@link ProjectInfo} id
     */
    private void notifyAboutPUMLCodeGeneration(Long id) {
        if (!emitterCache.contains(id, UML_CODE_GENERATION)) return;

        SseEmitter emitter = emitterCache.get(id, UML_CODE_GENERATION);
        sendEvent(
                emitter,
                SseEmitter.event()
                        .name("UmlCodeGeneration")
                        .data(Result.SUCCEEDED)
        );
    }

    /**
     * Tries to send provided event using the provided {@link SseEmitter}
     *
     * @param emitter Through which {@link SseEmitter.SseEventBuilder} event will be sent.
     * @param event   {@link SseEmitter.SseEventBuilder} event which will be sent.
     */
    private void sendEvent(SseEmitter emitter, SseEmitter.SseEventBuilder event) {
        try {
            emitter.send(event);
        } catch (IOException e) {
            logger.info("Unable to send event: {}", event, e);
        } finally {
            emitter.complete();
        }
    }
}
