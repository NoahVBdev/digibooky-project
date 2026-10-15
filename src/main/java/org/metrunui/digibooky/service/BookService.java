package org.metrunui.digibooky.service;

import org.metrunui.digibooky.domain.Book;
import org.metrunui.digibooky.domain.BookNotFoundException;
import org.metrunui.digibooky.exception.BookServiceException;
import org.metrunui.digibooky.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book save(Book book) {
        // Check if book already in db
        if (bookRepository.findByISBN(book.getIsbn()).isPresent()) {
            throw new BookServiceException("Book with this ISBN already exists. ISBN provided: " + book.getIsbn());
        }
        return bookRepository.save(book);
    }

    public Collection<Book> getAllBooks() {
        return bookRepository.getAllBooks();
    }

    public Book getBook(String id) {
        try {
            UUID idAsUUID = UUID.fromString(id);
            return bookRepository.getBook(idAsUUID).orElseThrow(() -> new BookNotFoundException("Book with id " + id + " not found."));
        } catch (IllegalArgumentException ex) {
            throw new BookServiceException("Book id is not in a correct format.");
        }
    }
}
