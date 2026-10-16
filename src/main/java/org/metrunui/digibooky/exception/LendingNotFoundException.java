package org.metrunui.digibooky.exception;

public class LendingNotFoundException extends RuntimeException {
    public LendingNotFoundException(String message) {
        super(message);
    }
}
