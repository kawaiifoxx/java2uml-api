package org.java2uml.java2umlapi.restControllers.exceptionHandlers;

import org.java2uml.java2umlapi.restControllers.error.ErrorResponse;
import org.java2uml.java2umlapi.restControllers.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("NullableProblems")
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);

    /**
     * Customize the response for MethodArgumentNotValidException.
     * <p>This method delegates to {@link #handleExceptionInternal}.
     *
     * @param ex      the exception
     * @param headers the headers to be written to the response
     * @param status  the selected response status
     * @param request the current request
     * @return a {@code ResponseEntity} instance
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatus status,
            WebRequest request
    ) {
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());
        errors.addAll(
                ex.getBindingResult().getGlobalErrors()
                        .stream()
                        .map(error -> error.getObjectName() + ": " + error.getDefaultMessage())
                        .collect(Collectors.toList())
        );
        var errorResponse = new ErrorResponse.Builder()
                .withTimestamp(LocalDateTime.now())
                .withReason(ex.getLocalizedMessage())
                .withHttpStatus(status)
                .withErrors(errors)
                .build();
        return handleExceptionInternal(ex, errorResponse, headers, status, request);
    }

    /**
     * Customize the response for MissingServletRequestParameterException.
     * <p>This method delegates to {@link #handleExceptionInternal}.
     *
     * @param ex      the exception
     * @param headers the headers to be written to the response
     * @param status  the selected response status
     * @param request the current request
     * @return a {@code ResponseEntity} instance
     */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        var errorResponse = new ErrorResponse.Builder()
                .withTimestamp(LocalDateTime.now())
                .withHttpStatus(status)
                .withError(ex.getParameterName() + " parameter is missing")
                .withReason(ex.getLocalizedMessage())
                .build();
        return new ResponseEntity<>(errorResponse, headers, status);
    }

    /**
     * Customize the response for NoHandlerFoundException.
     * <p>This method delegates to {@link #handleExceptionInternal}.
     *
     * @param ex      the exception
     * @param headers the headers to be written to the response
     * @param status  the selected response status
     * @param request the current request
     * @return a {@code ResponseEntity} instance
     * @since 4.0
     */
    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        var errorResponse = new ErrorResponse.Builder()
                .withTimestamp(LocalDateTime.now())
                .withHttpStatus(HttpStatus.NOT_FOUND)
                .withError("No handler found for " + ex.getHttpMethod() + " " + ex.getRequestURL())
                .withReason(ex.getLocalizedMessage())
                .build();
        return new ResponseEntity<>(errorResponse, headers, HttpStatus.NOT_FOUND);
    }

    /**
     * Customize the response for HttpRequestMethodNotSupportedException.
     * <p>This method logs a warning, sets the "Allow" header, and delegates to
     * {@link #handleExceptionInternal}.
     *
     * @param ex      the exception
     * @param headers the headers to be written to the response
     * @param status  the selected response status
     * @param request the current request
     * @return a {@code ResponseEntity} instance
     */
    @SuppressWarnings("ConstantConditions")
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        var builder = new StringBuilder();
        builder.append(ex.getMethod())
                .append(" method is not supported for this request. Supported methods are ");
        ex.getSupportedHttpMethods().forEach(httpMethod -> builder.append(httpMethod).append(" "));
        var errorResponse = new ErrorResponse.Builder()
                .withTimestamp(LocalDateTime.now())
                .withHttpStatus(HttpStatus.METHOD_NOT_ALLOWED)
                .withError(builder.toString())
                .withReason(ex.getLocalizedMessage())
                .build();
        logger.warn(ex.getMessage());
        return new ResponseEntity<>(errorResponse, headers, HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * Customize the response for HttpMediaTypeNotSupportedException.
     * <p>This method sets the "Accept" header and delegates to
     * {@link #handleExceptionInternal}.
     *
     * @param ex      the exception
     * @param headers the headers to be written to the response
     * @param status  the selected response status
     * @param request the current request
     * @return a {@code ResponseEntity} instance
     */
    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        var errorResponse = new ErrorResponse.Builder().withTimestamp(LocalDateTime.now())
                .withHttpStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .withError(ex.getContentType() + " media type not supported. Supported media type is application/zip")
                .withReason(ex.getLocalizedMessage())
                .build();

        return new ResponseEntity<>(errorResponse, headers, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    /**
     * Helper method to get {@link ErrorResponse}
     *
     * @param error   error message to be shown in error field.
     * @param status  {@link HttpStatus}
     * @param message reason for the exception.
     * @return a {@link ErrorResponse} instance
     */
    private ErrorResponse getErrorResponse(String error, HttpStatus status, String message) {
        return new ErrorResponse.Builder()
                .withTimestamp(LocalDateTime.now())
                .withError(error)
                .withHttpStatus(status)
                .withReason(message)
                .build();
    }

    /**
     * Specialized handler for {@link ProjectInfoNotFoundException}
     *
     * @param ex {@link ProjectInfoNotFoundException}
     * @return {@link ResponseEntity} with {@link ErrorResponse} in the body.
     */
    @ExceptionHandler({ProjectInfoNotFoundException.class})
    public ResponseEntity<Object> handleProjectInfoNotFound(ProjectInfoNotFoundException ex) {
        return new ResponseEntity<>(
                getErrorResponse("ProjectInfo not found.", HttpStatus.NOT_FOUND, ex.getLocalizedMessage()),
                new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    /**
     * Specialized handler for {@link BadRequest}
     *
     * @param ex {@link BadRequest}
     * @return {@link ResponseEntity} with {@link ErrorResponse} in the body.
     */
    @ExceptionHandler({BadRequest.class})
    public ResponseEntity<Object> handleBadRequest(BadRequest ex) {
        ErrorResponse errorResponse = getErrorResponse("Unable to understand request",
                HttpStatus.BAD_REQUEST, ex.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, new HttpHeaders(), errorResponse.getHttpStatus());
    }

    /**
     * Specialized handler for {@link CannotGenerateSourceException}
     *
     * @param ex {@link CannotGenerateSourceException}
     * @return {@link ResponseEntity} with {@link ErrorResponse} in the body.
     */
    @ExceptionHandler({CannotGenerateSourceException.class})
    public ResponseEntity<Object> handleCannotGenerateSource(CannotGenerateSourceException ex) {
        ErrorResponse errorResponse = getErrorResponse("Cannot generate source.",
                HttpStatus.INTERNAL_SERVER_ERROR, ex.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, new HttpHeaders(), errorResponse.getHttpStatus());
    }

    /**
     * Specialized handler for {@link CannotGenerateSVGException}
     *
     * @param ex {@link CannotGenerateSVGException}
     * @return {@link ResponseEntity} with {@link ErrorResponse} in the body.
     */
    @ExceptionHandler({CannotGenerateSVGException.class})
    public ResponseEntity<Object> handleCannotGenerateSVGException(CannotGenerateSVGException ex) {
        ErrorResponse errorResponse = getErrorResponse("Cannot generate svg",
                HttpStatus.INTERNAL_SERVER_ERROR, ex.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, new HttpHeaders(), errorResponse.getHttpStatus());
    }

    /**
     * Specialized handler for {@link ClassRelationNotFoundException}
     *
     * @param ex {@link ClassRelationNotFoundException}
     * @return {@link ResponseEntity} with {@link ErrorResponse} in the body.
     */
    @ExceptionHandler({ClassRelationNotFoundException.class})
    public ResponseEntity<Object> handleClassRelationNotFound(ClassRelationNotFoundException ex) {
        ErrorResponse errorResponse = getErrorResponse("ClassRelation Not found",
                HttpStatus.NOT_FOUND, ex.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, new HttpHeaders(), errorResponse.getHttpStatus());
    }

    /**
     * Specialized handler for {@link LightWeightNotFoundException}
     *
     * @param ex {@link LightWeightNotFoundException}
     * @return {@link ResponseEntity} with {@link ErrorResponse} in the body.
     */
    @ExceptionHandler({LightWeightNotFoundException.class})
    public ResponseEntity<Object> handleLightWeightNotFound(LightWeightNotFoundException ex) {
        ErrorResponse errorResponse = getErrorResponse("Unable to find requested LightWeight.",
                HttpStatus.NOT_FOUND, ex.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, new HttpHeaders(), errorResponse.getHttpStatus());
    }

    /**
     * Specialized handler for {@link MethodNameToMethodIdNotFoundException}
     *
     * @param ex {@link MethodNameToMethodIdNotFoundException}
     * @return {@link ResponseEntity} with {@link ErrorResponse} in the body.
     */
    @ExceptionHandler({MethodNameToMethodIdNotFoundException.class})
    public ResponseEntity<Object> handleMNTMNotFound(MethodNameToMethodIdNotFoundException ex) {
        ErrorResponse errorResponse = getErrorResponse("Unable to find requested map.",
                HttpStatus.NOT_FOUND, ex.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, new HttpHeaders(), errorResponse.getHttpStatus());
    }

    /**
     * Specialized handler for {@link ParsedComponentNotFoundException}
     *
     * @param ex {@link ParsedComponentNotFoundException}
     * @return {@link ResponseEntity} with {@link ErrorResponse} in the body.
     */
    @ExceptionHandler({ParsedComponentNotFoundException.class})
    public ResponseEntity<Object> handleParsedComponentNotFound(
            ParsedComponentNotFoundException ex) {
        ErrorResponse errorResponse = getErrorResponse("Unable to find requested ParsedComponent.",
                HttpStatus.NOT_FOUND, ex.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, new HttpHeaders(), errorResponse.getHttpStatus());
    }

    /**
     * Handles all the errors which does not have a specialized handler.
     *
     * @param ex {@link Exception} any exception which does not have specialized handler.
     * @return {@link ResponseEntity} with {@link ErrorResponse} in the body.
     */
    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleAll(Exception ex) {
        return new ResponseEntity<>(
                getErrorResponse(
                        "unidentified error occurred on the server.",
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        ex.getLocalizedMessage()
                ), new HttpHeaders(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}


