package com.becareful.becarefulserver.global.exception;

import com.becareful.becarefulserver.global.exception.exception.*;
import lombok.extern.slf4j.*;
import org.springframework.http.*;
import org.springframework.web.bind.*;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> domainException(DomainException e) {
        log.info(e.getMessage());
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorResponse> authException(final AuthException e) {
        log.info("AuthException: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            final MethodArgumentNotValidException ex) {
        log.info("MethodArgumentNotValidException: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(
                        ex.getBindingResult().getAllErrors().get(0).getDefaultMessage()));
    }

    @ExceptionHandler(SocialWorkerException.class)
    public ResponseEntity<ErrorResponse> socialWorkerException(SocialWorkerException e) {
        log.info("SocialWorkerException: {}", e.getMessage());
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(AssociationException.class)
    public ResponseEntity<ErrorResponse> associationException(AssociationException e) {
        log.info("AssociationException: {}", e.getMessage());
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknownException(Exception e) {
        log.error("Unknown exception: {} {}", e.getMessage(), e);
        return ResponseEntity.internalServerError().body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException e) {
        log.info("NotFoundException: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
    }
}
