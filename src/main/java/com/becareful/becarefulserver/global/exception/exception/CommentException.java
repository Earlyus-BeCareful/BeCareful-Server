package com.becareful.becarefulserver.global.exception.exception;

/**
 * Raised when comment related business rules are violated.
 */
public class CommentException extends DomainException {

    public CommentException(String message) {
        super(message);
    }
}

