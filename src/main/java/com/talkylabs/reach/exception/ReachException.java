package com.talkylabs.reach.exception;

public abstract class ReachException extends RuntimeException {

    private static final long serialVersionUID = 2516935680980388130L;

    public ReachException(final String message) {
        this(message, null);
    }

    public ReachException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
