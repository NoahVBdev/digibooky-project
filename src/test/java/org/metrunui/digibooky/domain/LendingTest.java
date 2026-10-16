package org.metrunui.digibooky.domain;

import org.junit.jupiter.api.Test;
import org.metrunui.digibooky.exception.LendingAlreadyReturnedException;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LendingTest {

    private static final LocalDate LENDING_DATE = LocalDate.of(2026, 10, 16);
    private static final LocalDate DUE_DATE = LocalDate.of(2026, 11, 6);

    private Lending newLending() {
        return new Lending(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), LENDING_DATE, DUE_DATE);
    }

    @Test
    void returnBook_whenActive_thenReturnDateIsSetAndLendingIsNoLongerActive() {
        Lending lending = newLending();
        LocalDate returnDate = LocalDate.of(2026, 11, 1);

        lending.returnBook(returnDate);

        assertThat(lending.getReturnDate()).isEqualTo(returnDate);
        assertThat(lending.isActive()).isFalse();
    }

    @Test
    void returnBook_whenAlreadyReturned_thenThrowsAndKeepsFirstReturnDate() {
        Lending lending = newLending();
        LocalDate firstReturnDate = LocalDate.of(2026, 11, 1);
        lending.returnBook(firstReturnDate);

        assertThatThrownBy(() -> lending.returnBook(LocalDate.of(2026, 11, 2)))
                .isInstanceOf(LendingAlreadyReturnedException.class)
                .hasMessage("This lending has already been returned");
        assertThat(lending.getReturnDate()).isEqualTo(firstReturnDate);
    }

    @Test
    void isReturnedLate_whenReturnedBeforeDueDate_thenFalse() {
        Lending lending = newLending();

        lending.returnBook(DUE_DATE.minusDays(1));

        assertThat(lending.isReturnedLate()).isFalse();
    }

    @Test
    void isReturnedLate_whenReturnedOnDueDate_thenFalse() {
        Lending lending = newLending();

        lending.returnBook(DUE_DATE);

        assertThat(lending.isReturnedLate()).isFalse();
    }

    @Test
    void isReturnedLate_whenReturnedAfterDueDate_thenTrue() {
        Lending lending = newLending();

        lending.returnBook(DUE_DATE.plusDays(1));

        assertThat(lending.isReturnedLate()).isTrue();
    }

    @Test
    void isReturnedLate_whenNotReturnedYet_thenFalse() {
        Lending lending = newLending();

        assertThat(lending.isReturnedLate()).isFalse();
    }

}