package org.java2uml.java2umlapi.restControllers;

import org.java2uml.java2umlapi.fileStorage.repository.ProjectInfoRepository;
import org.java2uml.java2umlapi.restControllers.exceptions.ProjectInfoNotFoundException;
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

import static org.java2uml.java2umlapi.restControllers.services.SSEEmitterCache.SSEventType.*;

/**
 * This controller enables client's to subscribe to events. A event is fired when file is parsed, A project model is
 * generated, uml is generated or a class diagram is generated.
 *
 * @author kawaiifoxx
 */
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

    @GetMapping("/parse/{projectInfoId}")
    public SseEmitter subscribeToParseEvent(@PathVariable("projectInfoId") Long id) {
        return produceEmitter(id, PARSE);
    }

    @GetMapping("/uml/code/{projectInfoId}")
    public SseEmitter subscribeToUMLCodeGenerationEvent(@PathVariable("projectInfoId") Long id) {
        return produceEmitter(id, UML_CODE_GENERATION);
    }

    @GetMapping("/uml/svg/{projectInfoId}")
    public SseEmitter subscribeToUMLSVGGenerationEvent(@PathVariable("projectInfoId") Long id) {
        return produceEmitter(id, UML_SVG_GENERATION);
    }

    @GetMapping("/source/{projectInfoId}")
    public SseEmitter subscribeToSourceGeneration(@PathVariable("projectInfoId") Long id) {
        return produceEmitter(id, SOURCE_GENERATION);
    }

    /**
     * Produces {@link SseEmitter} with given configurations.
     * Performs all the necessary housekeeping.
     *
     * @param id id of project.
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
        var emitter = new SseEmitter( 60 * 1000L);

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
