package com.learnvault.identityaccessmanagement.exception;

/** Thrown when an account cannot log in due to its status (inactive/locked/disabled). Maps to HTTP 403. */
public class AccountStatusException extends RuntimeException {
    public AccountStatusException(String message) {
        super(message);
    }
}
