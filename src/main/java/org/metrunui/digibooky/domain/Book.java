package org.metrunui.digibooky.domain;

import java.util.Objects;
import java.util.UUID;

public class Book {
    private static final int ISBN_LENGTH = 13;
    private static final String ISBN_VALID_PREFIX_1 = "978";
    private static final String ISBN_VALID_PREFIX_2 = "979";

    private final UUID bookId;
    private final String isbn;
    private final String title;
    private final String summary;
    private final String authorLastName;
    private final String authorFirstName;

    public Book(String isbn, String title, String summary, String authorLastName, String authorFirstName) {
        if (isbn.isBlank()) throw new BookException("isbn is required.");
        if (isbn.length() != ISBN_LENGTH) throw new BookException("isbn should be " + ISBN_LENGTH + " digits.");
        if (!isbn.startsWith(ISBN_VALID_PREFIX_1) && !isbn.startsWith(ISBN_VALID_PREFIX_2)) throw new BookException("isbn should begin with valid prefix.");

        if (title.isBlank()) throw new BookException("title is required.");
        if (authorLastName.isBlank()) throw new BookException("author last name is required.");
        if (authorFirstName.isBlank()) throw new BookException("author last name is required.");
        
        this.bookId = UUID.randomUUID();
        this.isbn = isbn;
        this.title = title;
        this.summary = summary;
        this.authorLastName = authorLastName;
        this.authorFirstName = authorFirstName;
    }

    public UUID getBookId() {
        return bookId;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public String getAuthorLastName() {
        return authorLastName;
    }

    public String getAuthorFirstName() {
        return authorFirstName;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(bookId, book.bookId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(bookId);
    }
}
