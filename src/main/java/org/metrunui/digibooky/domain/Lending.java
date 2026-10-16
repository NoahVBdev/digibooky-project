package org.metrunui.digibooky.domain;

import org.metrunui.digibooky.exception.LendingAlreadyReturnedException;

import java.time.LocalDate;
import java.util.UUID;

public class Lending {
    public static final int LENDING_PERIOD_WEEKS = 3;

    private final UUID id;
    private final UUID memberId;
    private final UUID bookId;
    private final LocalDate lendingDate;
    private final LocalDate dueDate;
    private LocalDate returnDate;

    public Lending(UUID memberId, UUID bookId, LocalDate lendingDate) {
        this(UUID.randomUUID(), memberId, bookId, lendingDate, lendingDate.plusWeeks(LENDING_PERIOD_WEEKS));
    }

    public Lending(UUID id, UUID memberId, UUID bookId, LocalDate lendingDate, LocalDate dueDate) {
        this.id = id;
        this.memberId = memberId;
        this.bookId = bookId;
        this.lendingDate = lendingDate;
        this.dueDate = dueDate;
    }

    public boolean isActive() {
        return returnDate == null;
    }

    public void returnBook(LocalDate returnDate) {
        if (!isActive()) {
            throw new LendingAlreadyReturnedException("This lending has already been returned");
        }
        this.returnDate = returnDate;
    }

    public boolean isReturnedLate() {
        return returnDate != null && returnDate.isAfter(dueDate);
    }

    public UUID getId() { return id; }
    public UUID getMemberId() { return memberId; }
    public UUID getBookId() { return bookId; }
    public LocalDate getLendingDate() { return lendingDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }


}
