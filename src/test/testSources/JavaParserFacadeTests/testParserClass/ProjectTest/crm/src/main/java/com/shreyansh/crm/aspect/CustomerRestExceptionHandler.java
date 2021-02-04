package com.shreyansh.crm.aspect;

import com.shreyansh.crm.error.exception.CustomerNotFoundException;
import com.shreyansh.crm.error.response.CustomerErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomerRestExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<CustomerErrorResponse> handleCustomerNotFound(CustomerNotFoundException e) {
        CustomerErrorResponse customerErrorResponse = new CustomerErrorResponse();

        customerErrorResponse.setMessage(e.getMessage());
        customerErrorResponse.setStatus(HttpStatus.NOT_FOUND.value());
        customerErrorResponse.setTimestamp(System.currentTimeMillis());

        return new ResponseEntity<>(customerErrorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<CustomerErrorResponse> handleAllException(Exception e) {
        CustomerErrorResponse customerErrorResponse = new CustomerErrorResponse();

        customerErrorResponse.setTimestamp(System.currentTimeMillis());
        customerErrorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        customerErrorResponse.setMessage(e.getMessage());

        return new ResponseEntity<>(customerErrorResponse, HttpStatus.BAD_REQUEST);
    }
}
