package org.metrunui.digibooky.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class BookTest {
    
    @Test
    void givenLegalValues_whenCreatingBook_thenBookCreated() {
        final String legalIsbn = "9780132350884";
        final String legalTitle = "Clean code";
        final String legalSummary = "Clean coding summary";
        final String legalAuthorFirstName = "Martin";
        final String legalAuthorLastName = "Robert";

        // Arrange
        Book book = new Book(legalIsbn, legalTitle, legalSummary, legalAuthorLastName, legalAuthorFirstName);

        // Assert
        Assertions.assertThat(book.getIsbn()).isEqualTo(legalIsbn);
        Assertions.assertThat(book.getTitle()).isEqualTo(legalTitle);
        Assertions.assertThat(book.getSummary()).isEqualTo(legalSummary);
        Assertions.assertThat(book.getAuthorFirstName()).isEqualTo(legalAuthorFirstName);
        Assertions.assertThat(book.getAuthorLastName()).isEqualTo(legalAuthorLastName);
    }

    @ParameterizedTest()
    @ValueSource(strings = {"", "  ", "1234567891014", "987456789101", "98745678910111"})
    void givenNonValidIsbn_whenCreatingBook_thenThrowsBookException(String isbn) {
        Assertions.assertThatThrownBy(() -> new Book(isbn, "Valid", "Valid", "Valid", "Valid")).isInstanceOf(BookException.class);
    }
}