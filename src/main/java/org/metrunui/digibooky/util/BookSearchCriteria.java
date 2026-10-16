package org.metrunui.digibooky.util;

public record BookSearchCriteria(
        String isbn,
        String title,
        String authorLastName,
        String authorFirstName ) {
}
