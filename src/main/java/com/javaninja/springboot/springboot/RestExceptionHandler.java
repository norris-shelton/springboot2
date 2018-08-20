package com.javaninja.springboot.springboot;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


/**
 * @author norris.shelton
 */
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Object> handleResponseStatus(ResponseStatusException e, WebRequest request) {

        logger.error(e.getReason(), e);

        return ResponseEntity.status(e.getStatus())
                             .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                             .body(e.getReason());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleBadRequest(Exception e, WebRequest request) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        return handleExceptionInternal(e,
                                       e.getLocalizedMessage(),
                                       httpHeaders,
                                       HttpStatus.INTERNAL_SERVER_ERROR,
                                       request);
    }
}
