package com.learnvault.identityaccessmanagement.exception;

/** Thrown when login credentials are invalid (unknown email or wrong password). Maps to HTTP 401. */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
