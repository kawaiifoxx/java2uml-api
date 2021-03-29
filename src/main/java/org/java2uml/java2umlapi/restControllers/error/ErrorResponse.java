package org.java2uml.java2umlapi.restControllers.error;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * This class should be used for sending error responses to the client.
 * </p>
 *
 * @author kawaiifox
 */
public class ErrorResponse {
    private LocalDateTime timestamp;
    private HttpStatus httpStatus;
    private String reason;
    private List<String> errors;

    /**
     * Should not be instantiated from outside.
     */
    private ErrorResponse() {
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getReason() {
        return reason;
    }

    public List<String> getErrors() {
        return errors;
    }

    /**
     * <p>
     * This is a builder for {@link ErrorResponse}.
     * </p>
     *
     * @author kawaiifox
     */
    public static class Builder {
        private final ErrorResponse errorResponse = new ErrorResponse();

        /**
         * Adds a timestamp to {@link ErrorResponse}
         *
         * @param timestamp {@link LocalDateTime} when error occurred.
         * @return Builder for adding more properties.
         */
        public Builder withTimestamp(LocalDateTime timestamp) {
            errorResponse.timestamp = timestamp;
            return this;
        }

        /**
         * Adds a http status to {@link ErrorResponse}
         *
         * @param httpStatus {@link HttpStatus} status code associated with the error.
         * @return Builder for adding more properties.
         */
        public Builder withHttpStatus(HttpStatus httpStatus) {
            errorResponse.httpStatus = httpStatus;
            return this;
        }

        /**
         * Adds the reason for the error to {@link ErrorResponse}
         *
         * @param reason should answer the question "Why error occurred?"
         * @return Builder for adding more properties.
         */
        public Builder withReason(String reason) {
            errorResponse.reason = reason;
            return this;
        }

        /**
         * Adds the error to the {@link ErrorResponse}
         *
         * @param error Description of the error
         * @return Builder for adding more properties.
         */
        public Builder withError(String error) {
            if (errorResponse.errors == null) {
                errorResponse.errors = new ArrayList<>();
            }
            errorResponse.errors.add(error);
            return this;
        }

        /**
         * Adds a list of errors to the {@link ErrorResponse}
         *
         * @param errors a list containing Description of the errors.
         * @return Builder for adding more properties.
         */
        public Builder withErrors(List<String> errors) {
            if (errorResponse.errors == null) {
                errorResponse.errors = errors;
                return this;
            }
            errorResponse.errors.addAll(errors);
            return this;
        }

        /**
         * @return Built error response.
         * @throws IllegalStateException if errorResponse has httpStatus as null or errors list is empty or null.
         */
        public ErrorResponse build() {
            if (errorResponse.httpStatus == null) {
                throw new IllegalStateException(
                        "httpStatus field of error response cannot be null, " +
                                "set HttpStatus with withHttpStatus(HttpStatus) method to get rid of this exception.");
            }

            if (errorResponse.errors == null || errorResponse.errors.isEmpty()) {
                throw new IllegalStateException(
                        "Errors list should at least contain one error description, use withError(String) method" +
                                " or withErrors(List<String>) method to get rid of this exception.");
            }
            return errorResponse;
        }
    }
}
