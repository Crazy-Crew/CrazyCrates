package com.badbones69.crazycrates.api.exception;

public class CratesException extends IllegalStateException {

    public CratesException(final String message, final Exception exception) {
        super(message, exception);
    }

    public CratesException(final String message) {
        super(message);
    }
}