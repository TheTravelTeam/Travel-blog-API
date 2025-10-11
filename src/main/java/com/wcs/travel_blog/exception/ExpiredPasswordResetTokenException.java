package com.wcs.travel_blog.exception;

public class ExpiredPasswordResetTokenException extends RuntimeException {

    public ExpiredPasswordResetTokenException(String message) {
        super(message);
    }
}
