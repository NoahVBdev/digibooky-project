package org.metrunui.digibooky.controllers;

public class ForbiddenException extends  RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
