package com.wcs.travel_blog.exception;

public class PseudoAlreadyExistException extends RuntimeException {
    public PseudoAlreadyExistException(String message) {
        super(message);
    }
}
