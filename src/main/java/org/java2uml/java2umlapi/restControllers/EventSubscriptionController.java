package org.java2uml.java2umlapi.restControllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.java2uml.java2umlapi.fileStorage.repository.ProjectInfoRepository;
import org.java2uml.java2umlapi.restControllers.exceptions.ProjectInfoNotFoundException;
import org.java2uml.java2umlapi.restControllers.response.ErrorResponse;
import org.java2uml.java2umlapi.restControllers.services.SSEEmitterCache;
import org.java2uml.java2umlapi.restControllers.services.SSEEmitterCache.SSEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

import static org.java2uml.java2umlapi.restControllers.SwaggerDescription.ERR_RESPONSE_MEDIA_TYPE;
import static org.java2uml.java2umlapi.restControllers.SwaggerDescription.INTERNAL_SERVER_ERROR_DESC;
import static org.java2uml.java2umlapi.restControllers.services.SSEEmitterCache.SSEventType.*;

/**
 * This controller enables client's to subscribe to events. A event is fired when file is parsed, A project model is
 * generated, uml is generated or a class diagram is generated.
 *
 * @author kawaiifoxx
 */
@Tag(name = "Server Sent Events", description = "Subscribe to Events occurring on the server")
@RestController
@RequestMapping("/api/event")
public class EventSubscriptionController {

    private final ProjectInfoRepository projectInfoRepository;
    private final SSEEmitterCache emitterCache;
    private final Logger logger = LoggerFactory.getLogger(EventSubscriptionController.class);

    public EventSubscriptionController(
            ProjectInfoRepository projectInfoRepository,
            SSEEmitterCache emitterCache) {
        this.projectInfoRepository = projectInfoRepository;
        this.emitterCache = emitterCache;
    }

    @Operation(summary = "Subscribe to Parse Event", description = "A parse event is fired when a file is parsed.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Subscription Successful"),
            @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR_DESC,
                    content = @Content(mediaType = ERR_RESPONSE_MEDIA_TYPE,
                            schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping("/parse/{projectInfoId}")
    public SseEmitter subscribeToParseEvent(@PathVariable("projectInfoId") Long id) {
        return produceEmitter(id, PARSE);
    }

    @Operation(summary = "Subscribe to Plant UML code Generation Event",
            description = "A plant uml code generation event is fired when Plant UML code is generated.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Subscription Successful"),
            @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR_DESC,
                    content = @Content(mediaType = ERR_RESPONSE_MEDIA_TYPE,
                            schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping("/uml/code/{projectInfoId}")
    public SseEmitter subscribeToUMLCodeGenerationEvent(@PathVariable("projectInfoId") Long id) {
        return produceEmitter(id, UML_CODE_GENERATION);
    }

    @Operation(summary = "Subscribe to UML Class Diagram Generation Event",
            description = "A class diagram generation event is fired when a class diagram is generated.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Subscription Successful"),
            @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR_DESC,
                    content = @Content(mediaType = ERR_RESPONSE_MEDIA_TYPE,
                            schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping("/uml/svg/{projectInfoId}")
    public SseEmitter subscribeToUMLSVGGenerationEvent(@PathVariable("projectInfoId") Long id) {
        return produceEmitter(id, UML_SVG_GENERATION);
    }

    @Operation(summary = "Subscribe to Project Model Generation Event",
            description = "A Project Model generation event is fired when Project Model is generated.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Subscription Successful"),
            @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR_DESC,
                    content = @Content(mediaType = ERR_RESPONSE_MEDIA_TYPE,
                            schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping("/source/{projectInfoId}")
    public SseEmitter subscribeToSourceGeneration(@PathVariable("projectInfoId") Long id) {
        return produceEmitter(id, SOURCE_GENERATION);
    }

    @GetMapping("/dependency-matrix/{projectInfoId}")
    public SseEmitter subscribeToDependencyMatrixGeneration(@PathVariable("projectInfoId")Long id) {
        return produceEmitter(id, DEPENDENCY_MATRIX_GENERATION);
    }

    /**
     * Produces {@link SseEmitter} with given configurations.
     * Performs all the necessary housekeeping.
     *
     * @param id        id of project.
     * @param eventType {@link SSEventType}
     * @return produced {@link SseEmitter}
     */
    private SseEmitter produceEmitter(Long id, SSEventType eventType) {
        assertThatProjectIsPresentWithGivenId(id);
        var emitter = getSseEmitter();

        emitterCache.save(id, eventType, emitter);
        emitter.onCompletion(() -> emitterCache.delete(id, eventType));
        return emitter;
    }

    /**
     * sends a subscribed event to client.
     *
     * @return {@link SseEmitter} with TIMEOUT set to 60s.
     */
    private SseEmitter getSseEmitter() {
        var emitter = new SseEmitter(60 * 1000L);

        try {
            emitter.send(SseEmitter.event().name("SUBSCRIBED"));
        } catch (IOException e) {
            logger.info("Unable to send subscribe event.", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to send events.");
        }
        return emitter;
    }

    /**
     * @param id for which you need to assert.
     */
    private void assertThatProjectIsPresentWithGivenId(Long id) {
        if (!projectInfoRepository.existsById(id))
            throw new ProjectInfoNotFoundException("Project Info with id: " + id + " is not present.");
    }
}
