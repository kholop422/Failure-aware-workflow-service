package com.workflow.failureawareworkflow.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(
            WorkflowNotFoundException.class
    )
    public ResponseEntity<String>
    handleWorkflowException(
            WorkflowNotFoundException ex
    ){

        return ResponseEntity
                .status(
                        HttpStatus.NOT_FOUND
                )
                .body(
                        ex.getMessage()
                );

    }

}
