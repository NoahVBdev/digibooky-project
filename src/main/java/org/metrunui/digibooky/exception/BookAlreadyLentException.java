package org.metrunui.digibooky.exception;

public class BookAlreadyLentException extends RuntimeException {
    public BookAlreadyLentException(String message) {
        super(message);
    }
}