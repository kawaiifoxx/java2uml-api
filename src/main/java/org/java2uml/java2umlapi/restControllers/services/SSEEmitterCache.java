package org.java2uml.java2umlapi.restControllers.services;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
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
    /**
     * Different types of events for which {@link SseEmitter} could be registered.
     *
     * @author kawaiifoxx
     */
    public enum SSEventType {PARSE, SOURCE_GENERATION, UML_CODE_GENERATION, UML_SVG_GENERATION}

    /**
     * A class for modeling a unique key for retrieving {@link SseEmitter} from cache.
     *
     * @author kawaiifoxx
     */
    private class Key {
        Long id;
        SSEventType type;

        public Key(Long id, SSEventType type) {
            this.id = id;
            this.type = type;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;

            if (!(o instanceof Key)) return false;

            Key key = (Key) o;

            return new EqualsBuilder().append(id, key.id).append(type, key.type).isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37).append(id).append(type).toHashCode();
        }
    }

    private final Map<Key, SseEmitter> cache = new ConcurrentHashMap<>();

    /**
     * Is there {@link SseEmitter} with corresponding id?
     *
     * @param id for which we need to check presence of {@link SseEmitter}
     * @param eventType {@link SSEventType} for which we need to check presence of {@link SseEmitter}
     * @return true if present, false otherwise.
     */
    public boolean contains(Long id, SSEventType eventType) {
        return cache.containsKey(new Key(id, eventType));
    }

    /**
     * Saves the {@link SseEmitter} with given id
     *
     * @param id      id of {@link SseEmitter}
     * @param eventType the {@link SSEventType} for which for which {@link SseEmitter} is required.
     * @param emitter {@link SseEmitter} you want to save
     * @return true if no other value was saved earlier with the same id
     */
    public boolean save(Long id, SSEventType eventType, SseEmitter emitter) {
        return cache.put(new Key(id, eventType), emitter) == null;
    }

    /**
     * @param id for which we want {@link SseEmitter}
     * @param eventType {@link SSEventType} for which emitter is required.
     * @return {@link SseEmitter} withe given id, null otherwise.
     */
    public SseEmitter get(Long id, SSEventType eventType) {
        return cache.get(new Key(id, eventType));
    }
}
