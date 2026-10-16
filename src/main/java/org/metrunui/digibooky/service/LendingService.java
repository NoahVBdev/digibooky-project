package org.metrunui.digibooky.service;

import org.metrunui.digibooky.repository.BookRepository;
import org.metrunui.digibooky.repository.LendingRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;

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
}