package com.xxc.entity.exp;

/**
 * @author xixincan
 * 2020-03-11
 * @version 1.0.0
 */
public class ValidException extends RuntimeException {

    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public ValidException(String message) {
        super(message);
    }
}
