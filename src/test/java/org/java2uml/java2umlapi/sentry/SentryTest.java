package org.java2uml.java2umlapi.sentry;

import io.sentry.Sentry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

//TODO: Enable sentry support in github.
@SpringBootTest
@DisplayName("Sentry Integration test.")
public class SentryTest {
    @Test
    @Tag("Sentry")
    @DisplayName("Sentry should capture exception.")
    void testSentryIntegration() {
        try {
            throw new Exception("This is a test.");
        } catch (Exception e) {
            Sentry.captureException(e);
        }
    }
}
