package org.metrunui.digibooky.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metrunui.digibooky.domain.Lending;
import org.metrunui.digibooky.exception.ForbiddenException;
import org.metrunui.digibooky.exception.LendingAlreadyReturnedException;
import org.metrunui.digibooky.exception.LendingNotFoundException;
import org.metrunui.digibooky.repository.BookRepository;
import org.metrunui.digibooky.repository.LendingRepository;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LendingServiceReturnTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 10, 16);
    private static final UUID MEMBER_ID = UUID.randomUUID();

    private LendingRepository lendingRepository;
    private LendingService lendingService;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(Instant.parse("2026-10-16T10:00:00Z"), ZoneId.of("Europe/Brussels"));
        lendingRepository = new LendingRepository();
        lendingService = new LendingService(lendingRepository, new BookRepository(), clock);
    }

    private Lending saveLending(UUID memberId, LocalDate dueDate) {
        Lending lending = new Lending(UUID.randomUUID(), memberId, UUID.randomUUID(),
                dueDate.minusWeeks(Lending.LENDING_PERIOD_WEEKS), dueDate);
        return lendingRepository.save(lending);
    }

    @Test
    void returnBook_whenOwnActiveLending_thenReturnDateIsTodayAndLendingIsSaved() {
        Lending lending = saveLending(MEMBER_ID, TODAY.plusDays(5));

        lendingService.returnBook(MEMBER_ID, lending.getId());

        Lending saved = lendingRepository.findById(lending.getId()).orElseThrow();
        assertThat(saved.getReturnDate()).isEqualTo(TODAY);
        assertThat(saved.isActive()).isFalse();
    }

    @Test
    void returnBook_whenReturnedBeforeDueDate_thenNotLate() {
        Lending lending = saveLending(MEMBER_ID, TODAY.plusDays(5));

        Lending returned = lendingService.returnBook(MEMBER_ID, lending.getId());

        assertThat(returned.isReturnedLate()).isFalse();
    }

    @Test
    void returnBook_whenReturnedAfterDueDate_thenLate() {
        Lending lending = saveLending(MEMBER_ID, TODAY.minusDays(3));

        Lending returned = lendingService.returnBook(MEMBER_ID, lending.getId());

        assertThat(returned.isReturnedLate()).isTrue();
    }

    @Test
    void returnBook_whenLendingDoesNotExist_thenThrowsLendingNotFoundException() {
        UUID unknownId = UUID.randomUUID();

        assertThatThrownBy(() -> lendingService.returnBook(MEMBER_ID, unknownId))
                .isInstanceOf(LendingNotFoundException.class)
                .hasMessage("Lending with id " + unknownId + " not found");
    }

    @Test
    void returnBook_whenLendingBelongsToAnotherMember_thenThrowsForbiddenAndLendingStaysActive() {
        Lending lending = saveLending(MEMBER_ID, TODAY.plusDays(5));
        UUID otherMemberId = UUID.randomUUID();

        assertThatThrownBy(() -> lendingService.returnBook(otherMemberId, lending.getId()))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("You can only return your own lendings");
        assertThat(lending.isActive()).isTrue();
    }

    @Test
    void returnBook_whenAlreadyReturned_thenThrowsLendingAlreadyReturnedException() {
        Lending lending = saveLending(MEMBER_ID, TODAY.plusDays(5));
        lendingService.returnBook(MEMBER_ID, lending.getId());

        assertThatThrownBy(() -> lendingService.returnBook(MEMBER_ID, lending.getId()))
                .isInstanceOf(LendingAlreadyReturnedException.class)
                .hasMessage("This lending has already been returned");
    }
}