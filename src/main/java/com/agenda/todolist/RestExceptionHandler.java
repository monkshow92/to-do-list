package com.agenda.todolist;

import com.agenda.todolist.exception.TaskNotFoundException;
import com.agenda.todolist.exception.ToDoListNotFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@RequestMapping(produces = "application/vnd.error+json")
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<VndErrors> handleValidationException(ConstraintViolationException ex) {
        String logRef = logDebug(ex);
        String bodyOfResponse = new ArrayList<>(ex.getConstraintViolations()).get(0).getMessage();
        return createErrorResponse(ex, HttpStatus.CONFLICT, logRef, bodyOfResponse);
    }

    @ExceptionHandler({ SQLException.class, DataAccessException.class, DataIntegrityViolationException.class })
    public ResponseEntity<VndErrors> databaseError(Exception ex) {
        String logRef = logDebug(ex);
        String bodyOfResponse = "Database error occurred";
        return createErrorResponse(ex, HttpStatus.CONFLICT, logRef, bodyOfResponse);
    }

    @ExceptionHandler({ToDoListNotFoundException.class, TaskNotFoundException.class})
    public ResponseEntity<VndErrors> handleToDoListNotFoundException(RuntimeException ex) {
        String logRef = logDebug(ex);
        if (ex instanceof ToDoListNotFoundException) {
            logRef = ((ToDoListNotFoundException) ex).getId();
        } else if (ex instanceof TaskNotFoundException){
            logRef = ((TaskNotFoundException) ex).getId();
        }
        return createErrorResponse(ex, HttpStatus.NOT_FOUND, logRef, "");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<VndErrors> assertionException(final IllegalArgumentException e) {
        return createErrorResponse(e, HttpStatus.NOT_FOUND, e.getLocalizedMessage(), "");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<VndErrors> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String logRef = logDebug(ex);
        String bodyOfResponse = String.format("The parameter '%s' of value '%s' could not be converted to type '%s'", ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName());
        return createErrorResponse(ex, HttpStatus.CONFLICT, logRef, bodyOfResponse);
    }

    // private HELPER METHODS

    private String logDebug(Throwable t) {
        logger.debug("Caught exception while handling a request", t);
        return t.getClass().getSimpleName();
    }

    private ResponseEntity<VndErrors> createErrorResponse(Exception ex, HttpStatus httpStatus, String logRef, String message) {
        logger.error("Request raised " + ex.getClass().getSimpleName());
        if (StringUtils.isEmpty(message)) {
            message = Optional.of(ex.getMessage()).orElse(ex.getClass().getSimpleName());
        }
        return new ResponseEntity<>(new VndErrors(logRef,  message), httpStatus);
    }

}
