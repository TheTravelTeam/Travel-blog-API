package com.wcs.travel_blog.exception;

public class InvalidPasswordResetRequestException extends RuntimeException {

    public InvalidPasswordResetRequestException(String message) {
        super(message);
    }
}
