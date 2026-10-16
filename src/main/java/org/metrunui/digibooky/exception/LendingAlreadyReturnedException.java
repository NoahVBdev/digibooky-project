package org.metrunui.digibooky.exception;

public class LendingAlreadyReturnedException extends RuntimeException {
    public LendingAlreadyReturnedException(String message) {
        super(message);
    }
}
