package com.robertwalker.openapidemo.handler;

import com.azure.core.exception.ResourceNotFoundException;
import com.robertwalker.openapidemo.exception.AzureConnectionFailure;
import com.robertwalker.openapidemo.model.Error;
import com.robertwalker.openapidemo.model.ErrorDetail;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

@RestControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(AzureConnectionFailure.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Error azureConnectionFailure(AzureConnectionFailure ex, WebRequest request) {
        return new Error("none", ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Error resourceNotFoundHandler(ResourceNotFoundException ex, WebRequest request) {
        ErrorDetail detail = new ErrorDetail(HttpStatus.NOT_FOUND.toString(), "Resource not found");
        Error error = new Error("none", HttpStatus.NOT_FOUND.toString(), ex.getMessage());
        error.setDetails(List.of(detail));
        return error;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Error globalExceptionHandler(Exception ex, WebRequest request) {
        return new Error("none", "UNEXPECTED_ERROR", ex.getMessage());
    }
}
