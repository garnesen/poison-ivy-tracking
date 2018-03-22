package com.hci_capstone.poison_ivy_tracker.exceptions;

/**
 * A custom exception for singletons that are not initialized.
 */
public class UninitializedSingletonException extends RuntimeException {
    public UninitializedSingletonException() { super(); }
    public UninitializedSingletonException(String message) { super(message); }
    public UninitializedSingletonException(String message, Throwable cause) { super(message, cause); }
    public UninitializedSingletonException(Throwable cause) { super(cause); }
}
