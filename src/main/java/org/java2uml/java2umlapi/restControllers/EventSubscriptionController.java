package org.java2uml.java2umlapi.restControllers;

import org.java2uml.java2umlapi.fileStorage.repository.ProjectInfoRepository;
import org.java2uml.java2umlapi.restControllers.exceptions.ProjectInfoNotFoundException;
import org.java2uml.java2umlapi.restControllers.services.SSEEmitterCache;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

import static org.java2uml.java2umlapi.restControllers.services.SSEEmitterCache.SSEventType.PARSE;

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
    private final Long TIMEOUT_MILLIS = 60 * 1000L;

    public EventSubscriptionController(
            ProjectInfoRepository projectInfoRepository,
            SSEEmitterCache emitterCache) {
        this.projectInfoRepository = projectInfoRepository;
        this.emitterCache = emitterCache;
    }

    @GetMapping("/parse/{projectInfoId}")
    public SseEmitter subscribeToParseEvent(@PathVariable("projectInfoId") Long id) {
        if (!projectInfoRepository.existsById(id))
            throw new ProjectInfoNotFoundException("Project Info with id: " + id + " is not present.");

        var emitter = new SseEmitter(TIMEOUT_MILLIS);

        try {
            emitter.send(SseEmitter.event().name("SUBSCRIBED"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        emitterCache.save(id, PARSE, emitter);
        emitter.onCompletion(() -> emitterCache.delete(id, PARSE));
        return emitter;
    }
}
