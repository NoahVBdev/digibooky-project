package org.metrunui.digibooky.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metrunui.digibooky.domain.Book;
import org.metrunui.digibooky.domain.BookNotFoundException;
import org.metrunui.digibooky.domain.Lending;
import org.metrunui.digibooky.exception.BookAlreadyLentException;
import org.metrunui.digibooky.repository.BookRepository;
import org.metrunui.digibooky.repository.LendingRepository;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LendingServiceLendTest {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2026-10-16T10:00:00Z"), ZoneId.of("Europe/Brussels"));
    private static final UUID MEMBER_ID = UUID.randomUUID();

    private BookRepository bookRepository;
    private LendingRepository lendingRepository;
    private LendingService lendingService;
    private Book book;

    @BeforeEach
    void setUp() {
        bookRepository = new BookRepository();
        lendingRepository = new LendingRepository();
        lendingService = new LendingService(lendingRepository, bookRepository, FIXED_CLOCK);
        book = bookRepository.save(new Book("9781234567897", "Test book", "A summary", "Doe", "John"));
    }

    @Test
    void lendBook_whenBookIsFree_thenCreatesLendingDueInThreeWeeks() {
        Lending lending = lendingService.lendBook(MEMBER_ID, book.getIsbn());

        assertThat(lending.getMemberId()).isEqualTo(MEMBER_ID);
        assertThat(lending.getBookId()).isEqualTo(book.getBookId());
        assertThat(lending.getLendingDate()).isEqualTo(LocalDate.of(2026, 10, 16));
        assertThat(lending.getDueDate()).isEqualTo(LocalDate.of(2026, 11, 6));
        assertThat(lendingRepository.findById(lending.getId())).contains(lending);
    }

    @Test
    void lendBook_whenIsbnUnknown_thenThrowsBookNotFound() {
        assertThatThrownBy(() -> lendingService.lendBook(MEMBER_ID, "9789999999999"))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void lendBook_whenBookAlreadyLent_thenThrowsBookAlreadyLent() {
        lendingService.lendBook(MEMBER_ID, book.getIsbn());

        assertThatThrownBy(() -> lendingService.lendBook(UUID.randomUUID(), book.getIsbn()))
                .isInstanceOf(BookAlreadyLentException.class)
                .hasMessage("This book is already lent");
    }
}