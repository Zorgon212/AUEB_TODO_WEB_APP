package com.pireaus.todoWebApp.common.exception;

// a domain-level "no such thing" - replaces the ad-hoc RuntimeException that
// used to get thrown from orElseThrow(...) all over the controllers.
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
