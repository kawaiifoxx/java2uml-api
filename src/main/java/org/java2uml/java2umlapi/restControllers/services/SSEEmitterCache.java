package org.java2uml.java2umlapi.restControllers.services;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A normal cache service. Thread Safe, provides normal crud operations.
 * Nothing fancy.
 *
 * @author kawaiifox
 */
@Service
public class SSEEmitterCache {
    private final Map<Long, SseEmitter> cache = new ConcurrentHashMap<>();

    /**
     * Is there {@link SseEmitter} with corresponding id?
     * @param id for which we need to check presence of {@link SseEmitter}
     * @return true if present, false otherwise.
     */
    public boolean contains(Long id) {
        return cache.containsKey(id);
    }

    /**
     * Saves the {@link SseEmitter} with given id
     *
     * @param id id of {@link SseEmitter}
     * @param emitter {@link SseEmitter} you want to save
     * @return true if no other value was saved earlier with the same id
     */
    public boolean save(Long id, SseEmitter emitter) {
        return cache.put(id, emitter) == null;
    }

    /**
     * @param id for which we want {@link SseEmitter}
     * @return {@link SseEmitter} withe given id.
     */
    public SseEmitter get(Long id) {
        return cache.get(id);
    }
}
