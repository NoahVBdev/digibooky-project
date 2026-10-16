package org.metrunui.digibooky.service;

import org.metrunui.digibooky.domain.Book;
import org.metrunui.digibooky.domain.BookNotFoundException;
import org.metrunui.digibooky.domain.Lending;
import org.metrunui.digibooky.exception.BookAlreadyLentException;
import org.metrunui.digibooky.repository.BookRepository;
import org.metrunui.digibooky.repository.LendingRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class LendingService {
    private final LendingRepository lendingRepository;
    private final BookRepository bookRepository;
    private final Clock clock;

    public LendingService(LendingRepository lendingRepository, BookRepository bookRepository, Clock clock) {
        this.lendingRepository = lendingRepository;
        this.bookRepository = bookRepository;
        this.clock = clock;
    }

    public Lending lendBook(UUID memberId, String isbn) {
        Book book = bookRepository.findByISBN(isbn)
                .orElseThrow(() -> new BookNotFoundException("Book with ISBN " + isbn + " not found"));

        if (lendingRepository.findActiveByBookId(book.getBookId()).isPresent()) {
            throw new BookAlreadyLentException("This book is already lent");
        }

        Lending lending = new Lending(memberId, book.getBookId(), LocalDate.now(clock));
        return lendingRepository.save(lending);
    }
}