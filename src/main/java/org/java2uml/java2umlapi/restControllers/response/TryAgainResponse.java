package org.java2uml.java2umlapi.restControllers.response;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * A simple response class.
 *
 * @author kawaiifox
 */
public class TryAgainResponse {
    private final String message;
    private final HttpStatus status;
    private final LocalDateTime timestamp;

    public TryAgainResponse(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
