package org.java2uml.java2umlapi.restControllers.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.java2uml.java2umlapi.restControllers.services.SSEEmitterCache.SSEventType.*;

@SpringBootTest
@DisplayName("When using SSEEmitterCache, ")
class SSEEmitterCacheTest {

    @Autowired
    SSEEmitterCache cache;

    @BeforeEach
    void setUp() {
        assertThat(cache).isNotNull();
        cache.save(1L, PARSE, new SseEmitter());
        cache.save(1L, UML_CODE_GENERATION, new SseEmitter());
    }

    @Test
    @DisplayName("given that SseEmitter is present for the given event with given id, contains should return true.")
    void contains1() {
        assertThat(cache.contains(1L, PARSE)).isTrue();
    }

    @Test
    @DisplayName("given that SseEmitter is not present for the given event with given id, contains should return false.")
    void contains2() {
        assertThat(cache.contains(2L, PARSE)).isFalse();
    }

    @Test
    @DisplayName("given that SseEmitter is not present for the given event with given id, contains should return false.")
    void contains3() {
        assertThat(cache.contains(1L, SOURCE_GENERATION)).isFalse();
    }

    @Test
    @DisplayName("given id and SSEventType should cache should save the emitter.")
    void save() {
        var emitter = new SseEmitter();
        cache.save(4L, PARSE, emitter);
        var retrieved = cache.get(4L, PARSE);

        assertThat(retrieved).isSameAs(emitter);
    }

    @Test
    @DisplayName("given id and SSEventType, if the emitter is present then return it.")
    void get() {
        assertThat(cache.get(1L, PARSE)).isNotNull();
    }

    @Test
    @DisplayName("given id and SSEventType, if the emitter is not present then return null.")
    void get2() {
        assertThat(cache.get(9L, PARSE)).isNull();
    }
}